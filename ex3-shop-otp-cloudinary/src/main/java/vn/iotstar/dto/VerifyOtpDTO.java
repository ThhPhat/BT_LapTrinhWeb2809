package vn.iotstar.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VerifyOtpDTO {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6, max = 6, message = "OTP gồm 6 số")
    private String otp;
}
