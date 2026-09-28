package vn.iotstar.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

            // User mẫu thường (đăng nhập bằng user01 hoặc user01@gmail.com / 123456)
            if (userRepository.findByUsername("user01").isEmpty()) {
                userRepository.save(User.builder()
                        .username("user01")
                        .email("user01@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Nguyễn Hữu Trung")
                        .images("/images/user.svg")
                        .role(userRole)
                        .enabled(true)
                        .build());
            }

            // User mẫu Admin (đăng nhập bằng admin hoặc admin@iotstar.vn / 123456)
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(User.builder()
                        .username("admin")
                        .email("admin@iotstar.vn")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Quản Trị Viên")
                        .images("/images/admin.svg")
                        .role(adminRole)
                        .enabled(true)
                        .build());
            }
        };
    }
}
