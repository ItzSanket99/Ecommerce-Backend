package com.ecommerce.project.service;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.AppRoles;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AuthenticationResult;
import com.ecommerce.project.payload.UserDTO;
import com.ecommerce.project.payload.UserResponse;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AuthenticationResult login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(),userDetails.getEmail(),roles,jwtCookie.toString());
        AuthenticationResult authenticationResult = new AuthenticationResult(response,jwtCookie);

        return authenticationResult;
    }

    @Override
    public ResponseEntity<MessageResponse> register(SignupRequest signupRequest) {
        if (userRepository.existsByUserName((signupRequest.getUsername()))) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: username is already taken!"));

        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));

        }
        User user = new User(
                signupRequest.getEmail(),
                signupRequest.getUsername(),
                encoder.encode(signupRequest.getPassword())
        );
        Set<String> StrRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (StrRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not Found"));
            roles.add(userRole);
        } else {
            StrRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRoles.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not Found"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRoles.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not Found"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not Found"));
                        roles.add(userRole);
                        break;
                }

            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User register successfully!"));
    }

    @Override
    public UserInfoResponse getCurrentUserDetails(Authentication authentication) {

        UserDetailsImpl userDetails =  (UserDetailsImpl)authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(),roles);
        return response;
    }

    @Override
    public ResponseCookie logoutUser() {
        return jwtUtils.getClearJwtCookie();
    }

    @Override
    public UserResponse getAllSellers(Pageable pageable) {
        Page<User> allUser = userRepository.findByRoleName(AppRoles.ROLE_SELLER,pageable);
        List<UserDTO> userDTOS = allUser.getContent().stream()
                .map(user -> modelMapper.map(user,UserDTO.class))
                .toList();

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDTOS);
        userResponse.setPageNumber(allUser.getNumber());
        userResponse.setPageSize(allUser.getSize());
        userResponse.setTotalPages(allUser.getTotalPages());
        userResponse.setTotalElements(allUser.getTotalElements());
        userResponse.setLastPage(allUser.isLast());
        return userResponse;
    }
}
