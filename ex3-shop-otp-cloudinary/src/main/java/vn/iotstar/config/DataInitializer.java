package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

/** Tự tạo ROLE_USER, ROLE_ADMIN và tài khoản admin mặc định (không cần chạy SQL tay). */
@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initData(RoleRepository roles, UserRepository users, PasswordEncoder encoder,
                               @Value("${app.admin.username}") String username,
                               @Value("${app.admin.email}") String email,
                               @Value("${app.admin.password}") String password) {
        return args -> {
            roles.findByName("ROLE_USER").orElseGet(() -> roles.save(Role.builder().name("ROLE_USER").build()));
            Role admin = roles.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roles.save(Role.builder().name("ROLE_ADMIN").build()));
            if (!users.existsByUsername(username) && !users.existsByEmail(email)) {
                users.save(User.builder().username(username).email(email)
                        .password(encoder.encode(password)).fullName("System Administrator")
                        .enabled(true).role(admin).build());
            }
        };
    }
}
