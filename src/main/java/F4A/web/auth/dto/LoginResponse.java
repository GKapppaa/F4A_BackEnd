package F4A.web.auth.dto;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEnSegundos,
        String correo,
        String rol
) {}
