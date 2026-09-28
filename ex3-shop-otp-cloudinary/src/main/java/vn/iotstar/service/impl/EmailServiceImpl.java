package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.iotstar.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Value("${app.otp.log-to-console:false}")
    private boolean logToConsole;

    @Override
    public void sendOtp(String email, String otp, String subject) {
        if (logToConsole) log.warn("[DEV] OTP cho {} = {}", email, otp);
        SimpleMailMessage message = new SimpleMailMessage();
        if (from != null && !from.isBlank()) message.setFrom(from);
        message.setTo(email);
        message.setSubject(subject);
        message.setText("""
                Xin chào,

                Mã OTP của bạn là: %s

                OTP có hiệu lực trong 5 phút và chỉ sử dụng một lần.
                Không chia sẻ mã này cho người khác.
                """.formatted(otp));
        try {
            mailSender.send(message);
        } catch (Exception e) {
            if (!logToConsole) throw new IllegalStateException("Gửi email thất bại, kiểm tra cấu hình SMTP", e);
            log.warn("Gửi mail thất bại (đang ở chế độ DEV, dùng OTP in ở console): {}", e.getMessage());
        }
    }
}
