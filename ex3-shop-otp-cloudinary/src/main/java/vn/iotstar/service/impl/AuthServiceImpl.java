package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.AuthService;
import vn.iotstar.service.OtpService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Override @Transactional
    public void register(RegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword()))
            throw new IllegalArgumentException("Mật khẩu xác nhận không đúng");
        User existing = userRepository.findByEmail(dto.getEmail()).orElse(null);
        if (existing != null && existing.isEnabled())
            throw new IllegalArgumentException("Email đã tồn tại");
        boolean usernameTaken = existing == null
                ? userRepository.existsByUsername(dto.getUsername())
                : userRepository.existsByUsernameAndIdNot(dto.getUsername(), existing.getId());
        if (usernameTaken) throw new IllegalArgumentException("Username đã tồn tại");

        if (existing == null) {
            Role role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalStateException("Chưa có ROLE_USER"));
            existing = User.builder().role(role).enabled(false).build();
        }
        // đăng ký lại khi chưa xác thực OTP: cập nhật thông tin và gửi OTP mới
        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setFullName(dto.getFullName());
        existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(existing);
        otpService.sendRegisterOtp(dto.getEmail());
    }

    @Override @Transactional
    public void resendRegisterOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        if (user.isEnabled()) throw new IllegalArgumentException("Tài khoản đã được xác thực");
        otpService.sendRegisterOtp(email);
    }

    @Override @Transactional
    public boolean verifyRegister(String email, String otp) {
        if (!otpService.verifyRegisterOtp(email, otp)) return false;
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        user.setEnabled(true);
        return true;
    }

    @Override @Transactional
    public void forgotPassword(String email) {
        if (!userRepository.existsByEmail(email)) throw new IllegalArgumentException("Email không tồn tại");
        otpService.sendResetPasswordOtp(email);
    }

    @Override
    public boolean verifyResetOtp(String email, String otp) {
        return otpService.verifyResetPasswordOtp(email, otp);
    }

    @Override @Transactional
    public void resetPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        user.setPassword(passwordEncoder.encode(password));
    }
}
