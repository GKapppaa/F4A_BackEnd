package F4A.web.auth.service;

import F4A.web.auth.dto.RegisterRequest;
import F4A.web.role.model.Role;
import F4A.web.role.repository.RoleRepository;
import F4A.web.user.model.User;
import F4A.web.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@Validated
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(@Valid RegisterRequest request) {
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo");
        }

        Role role = roleRepository.findByNombreIgnoreCase("CLIENTE")
                .or(() -> roleRepository.findByNombreIgnoreCase("ROLE_CLIENTE"))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No está configurado el rol CLIENTE"
                ));

        User user = new User();
        user.setNombre(request.nombre().trim());
        user.setApellidos(request.apellidos().trim());
        user.setCorreo(correo);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRol(role);
        return userRepository.save(user);
    }
}
