package org.example.backend.config;

import org.example.backend.entity.Account;
import org.example.backend.entity.Mentor;
import org.example.backend.entity.Intern;
import org.example.backend.repository.AccountRepository;
import org.example.backend.repository.MentorRepository;
import org.example.backend.repository.InternRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initDatabase(
            AccountRepository accountRepository,
            MentorRepository mentorRepository,
            InternRepository internRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Tài khoản Admin
            if (accountRepository.findByUsername("admin@example.com").isEmpty()) {
                Account admin = Account.builder()
                        .username("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Account.Role.ADMIN)
                        .build();
                accountRepository.save(admin);
                System.out.println("✅ Tài khoản ADMIN tạo thành công: admin@example.com / admin123");
            }

            // Tài khoản Mentor
            if (accountRepository.findByUsername("mentor@example.com").isEmpty()) {
                Account mentorAcc = Account.builder()
                        .username("mentor@example.com")
                        .password(passwordEncoder.encode("mentor123"))
                        .role(Account.Role.MENTOR)
                        .build();
                // Không cần accountRepository.save(mentorAcc) vì Mentor có CascadeType.ALL

                Mentor mentor = Mentor.builder()
                        .fullName("Nguyễn Văn Mentor")
                        .email("mentor@example.com")
                        .department("Công nghệ thông tin")
                        .position("Senior Developer")
                        .account(mentorAcc)
                        .build();
                mentorRepository.save(mentor);
                System.out.println("✅ Tài khoản MENTOR tạo thành công: mentor@example.com / mentor123");
            }

            // Tài khoản User (Intern)
            if (accountRepository.findByUsername("user@example.com").isEmpty()) {
                Account internAcc = Account.builder()
                        .username("user@example.com")
                        .password(passwordEncoder.encode("user123"))
                        .role(Account.Role.USER)
                        .build();
                // Không cần accountRepository.save(internAcc) vì Intern có CascadeType.ALL

                Intern intern = Intern.builder()
                        .fullName("Trần Thị User")
                        .email("user@example.com")
                        .status("ACTIVE")
                        .university("Đại học Bách Khoa")
                        .major("Kỹ thuật phần mềm")
                        .account(internAcc)
                        .build();
                internRepository.save(intern);
                System.out.println("✅ Tài khoản USER (Intern) tạo thành công: user@example.com / user123");
            }
        };
    }
}
