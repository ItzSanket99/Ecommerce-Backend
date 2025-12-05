package com.ecommerce.project.payload;

import com.ecommerce.project.security.response.UserInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Data
@AllArgsConstructor
public class AuthenticationResult {
    private final UserInfoResponse response;
    private final ResponseCookie jwtCookie;
}
