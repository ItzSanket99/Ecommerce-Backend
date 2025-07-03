package com.ecommerce.project.security.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String jwtToken;
    private List<String> roles;
}
