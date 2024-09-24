package com.anlv.security.email;

import com.anlv.security.permission.Permission;
import com.anlv.security.permission.PermissionRequest;
import com.anlv.security.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${pre-api}/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    @PostMapping("/{email}")
    public ResponseEntity<?> createRule(@PathVariable String email) {
        emailService.sendOtp(email, StringUtil.generateOtp());
        return ResponseEntity.ok("oce");
    }
}
