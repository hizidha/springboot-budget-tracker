package com.devland.assignment.finalproject.authentication;

import com.devland.assignment.finalproject.applicationuser.ApplicationUserService;
import com.devland.assignment.finalproject.applicationuser.model.ApplicationUser;
import com.devland.assignment.finalproject.authentication.exception.UnauthorizedException;
import com.devland.assignment.finalproject.authentication.jwt.JwtProvider;
import com.devland.assignment.finalproject.authentication.model.AuthenticationService;
import com.devland.assignment.finalproject.authentication.model.dto.*;
import com.devland.assignment.finalproject.expensecategory.ExpenseCategoryService;
import com.devland.assignment.finalproject.incomecategory.IncomeCategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final ApplicationUserService applicationUserService;
    private final JwtProvider jwtProvider;

    private final IncomeCategoryService incomeCategoryService;
    private final ExpenseCategoryService expenseCategoryService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO loginRequestDTO
    ) {
        Authentication authentication = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),
                                                                      loginRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = this.jwtProvider.generateJwtToken(authentication);

        ApplicationUser existingUser = this.applicationUserService.findByUsername(loginRequestDTO.getUsername());
        UserLoginResponseDTO user = new UserLoginResponseDTO(existingUser.getUsername(),
                                                             existingUser.getEmail(),
                                                             existingUser.getName(),
                                                             existingUser.getTotalBalance());

        return ResponseEntity.ok(new JwtResponseDTO(jwt, user));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @RequestBody @Valid RegisterRequestDTO registerRequestDTO
    ) {
        ApplicationUser newUser = registerRequestDTO.convertToEntity();
        ApplicationUser savedUser = this.applicationUserService.create(newUser);

        this.incomeCategoryService.createDefaultCategories(savedUser);
        this.expenseCategoryService.createDefaultCategories(savedUser);

        UserResponseDTO existingUser = new UserResponseDTO(savedUser.getUsername(),
                                                           savedUser.getEmail(),
                                                           savedUser.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponseDTO(existingUser));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{username}/profiles")
    public ResponseEntity<UserProfileResponseDTO> getOne(
            @PathVariable("username") String username
    ) {
        this.checkCredential(username);

        ApplicationUser existingUser = this.applicationUserService.findByUsername(username);
        UserProfileResponseDTO userDetailResponseDTO = existingUser.convertToResponse();

        return ResponseEntity.ok(userDetailResponseDTO);
    }

    private void checkCredential(String username) {
        if (!authenticationService.checkCredential(username)) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }
}