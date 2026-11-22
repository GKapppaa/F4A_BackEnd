package F4A.web.auth.controller;

import F4A.web.auth.dto.LoginRequest;
import F4A.web.auth.dto.LoginResponse;
import F4A.web.auth.dto.RegisterRequest;
import F4A.web.auth.service.AuthService;
import F4A.web.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuthService authService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.correo().trim(), request.password())
        );
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_CLIENTE")
                .replaceFirst("^ROLE_", "");

        return new LoginResponse(
                jwtService.createToken((org.springframework.security.core.userdetails.UserDetails)
                        authentication.getPrincipal()),
                "Bearer",
                jwtService.getExpirationSeconds(),
                authentication.getName(),
                role
        );
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return login(new LoginRequest(request.correo(), request.password()));
    }
}
