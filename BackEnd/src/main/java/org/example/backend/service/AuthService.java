package org.example.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.LoginResponse;
import org.example.backend.dto.GoogleLoginRequest;
import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.entity.Intern;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.repository.InternRepository;
import org.example.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private InternRepository internRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String GOOGLE_CLIENT_ID = "239106531712-f1if0c9rnbcnimm30vbumnj7cr6abk0b.apps.googleusercontent.com";

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(loginRequest.getUsername());

        Account account = accountRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        String email = "";
        String avatar = "";

        if (account.getRole() == Account.Role.MENTOR) {
            Optional<Mentor> mentor = mentorRepository.findByAccountUsername(account.getUsername());
            if (mentor.isPresent()) {
                email = mentor.get().getEmail();
                avatar = mentor.get().getAvatar();
            }
        } else if (account.getRole() == Account.Role.USER) {
            Optional<Intern> intern = internRepository.findByAccountUsername(account.getUsername());
            if (intern.isPresent()) {
                email = intern.get().getEmail();
                avatar = intern.get().getAvatar();
            }
        }

        return LoginResponse.builder()
                .token(jwt)
                .username(account.getUsername())
                .email(email)
                .avatar(avatar)
                .role(account.getRole().name())
                .build();
    }

    public LoginResponse loginWithGoogle(GoogleLoginRequest googleRequest) {
        System.out.println("Attempting Google token verification...");
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleRequest.getIdToken());
            if (idToken == null) {
                throw new RuntimeException("Invalid ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String googleId = payload.getSubject();

            Optional<Account> accountOptional = accountRepository.findByUsername(email);
            Account account;

            if (accountOptional.isPresent()) {
                account = accountOptional.get();
                if (account.getGoogleId() == null) {
                    account.setGoogleId(googleId);
                    accountRepository.save(account);
                }
            } else {
                account = Account.builder()
                        .username(email)
                        .googleId(googleId)
                        .role(Account.Role.USER)
                        .build();
            // No need to save account independently, Intern cascades persist
            
            // Create default Intern profile for new Google users
                Intern intern = Intern.builder()
                        .fullName(name)
                        .email(email)
                        .avatar(pictureUrl)
                        .account(account)
                        .status("PENDING")
                        .build();
                internRepository.save(intern);
            }

            // Fetch profile info for response
            String resEmail = email;
            String resAvatar = pictureUrl;
            
            if (account.getRole() == Account.Role.MENTOR) {
                mentorRepository.findByAccountUsername(account.getUsername()).ifPresent(m -> {
                    // email and avatar already set from google, but can override if needed
                });
            } else {
                internRepository.findByAccountUsername(account.getUsername()).ifPresent(i -> {
                    // email and avatar already set from google
                });
            }

            String jwt = jwtUtil.generateJwtToken(account.getUsername());

            return LoginResponse.builder()
                    .token(jwt)
                    .username(account.getUsername())
                    .email(resEmail)
                    .avatar(resAvatar)
                    .role(account.getRole().name())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Google Authentication failed", e);
        }
    }
}
