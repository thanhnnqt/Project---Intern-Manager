package org.example.backend.service.impl;

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
import org.example.backend.service.IAuthService;
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
public class AuthService implements IAuthService {
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final MentorRepository mentorRepository;
    private final InternRepository internRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private static final String GOOGLE_CLIENT_ID = "239106531712-f1if0c9rnbcnimm30vbumnj7cr6abk0b.apps.googleusercontent.com";

    public AuthService (AuthenticationManager authenticationManager, AccountRepository accountRepository,
    MentorRepository mentorRepository, InternRepository internRepository, PasswordEncoder encoder, JwtUtil jwtUtil){
        this.accountRepository = accountRepository;
        this.authenticationManager = authenticationManager;
        this.mentorRepository = mentorRepository;
        this.internRepository = internRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

     // Xác thực người dùng bằng username và password.

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(loginRequest.getUsername());

        Account account = accountRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        String email = "";
        String avatar = "";
        String fullName = "";

        if (account.getRole() == Account.Role.MENTOR) {
            Optional<Mentor> mentor = mentorRepository.findByAccountUsername(account.getUsername());
            if (mentor.isPresent()) {
                email = mentor.get().getEmail();
                avatar = mentor.get().getAvatar();
                fullName = mentor.get().getFullName();
            }
        } else if (account.getRole() == Account.Role.USER) {
            Optional<Intern> intern = internRepository.findByAccountUsername(account.getUsername());
            if (intern.isPresent()) {
                email = intern.get().getEmail();
                avatar = intern.get().getAvatar();
                fullName = intern.get().getFullName();
            }
        } else if (account.getRole() == Account.Role.ADMIN) {
            fullName = "Administrator";
            email = account.getUsername();
        }

        return LoginResponse.builder()
                .token(jwt)
                .username(account.getUsername())
                .email(email)
                .fullName(fullName)
                .avatar(avatar)
                .role(account.getRole().name())
                .build();
    }


     // Đăng nhập bằng Google ID Token.

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
            if (accountOptional.isEmpty()) {
                System.out.println("❌ Google Login: Unauthorized email attempt - " + email);
                throw new RuntimeException("Email này chưa có trong hệ thống. Vui lòng liên hệ Quản trị viên.");
            }

            Account account = accountOptional.get();
            if (account.getGoogleId() == null) {
                account.setGoogleId(googleId);
                accountRepository.save(account);
            }

            String resEmail = email;
            String resAvatar = pictureUrl;
            
            if (account.getRole() == Account.Role.MENTOR) {
                mentorRepository.findByAccountUsername(account.getUsername()).ifPresent(m -> {
                });
            } else {
                internRepository.findByAccountUsername(account.getUsername()).ifPresent(i -> {
                });
            }

            String jwt = jwtUtil.generateJwtToken(account.getUsername());

            return LoginResponse.builder()
                    .token(jwt)
                    .username(account.getUsername())
                    .email(resEmail)
                    .fullName(name)
                    .avatar(resAvatar)
                    .role(account.getRole().name())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Google Authentication failed", e);
        }
    }
}
