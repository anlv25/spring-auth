package com.anlv.security.otp;

import com.anlv.security.permission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    void deleteAllByEmailAndOtpType(String email,OtpType otpType);

    Optional<OTP> findFirstByEmailAndOtpAndOtpType(String email, String otp, OtpType otpType);
}
