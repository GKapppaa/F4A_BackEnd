package F4A.web.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(max = 100)
        @Pattern(regexp = "^[\\p{L}\\p{M}][\\p{L}\\p{M} '\\u2019-]{0,99}$",
                message = "El nombre solo puede contener letras, espacios, guiones y apóstrofes")
        String nombre,
        @NotBlank
        @Size(max = 100)
        @Pattern(regexp = "^[\\p{L}\\p{M}][\\p{L}\\p{M} '\\u2019-]{0,99}$",
                message = "Los apellidos solo pueden contener letras, espacios, guiones y apóstrofes")
        String apellidos,
        @NotBlank
        @Email(message = "El correo debe tener un formato válido")
        @Size(max = 255)
        String correo,
        @NotBlank
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password
) {}
