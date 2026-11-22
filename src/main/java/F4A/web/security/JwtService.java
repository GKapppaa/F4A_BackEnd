package F4A.web.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret:}") String secret,
            @Value("${app.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET debe contener al menos 32 bytes");
        }
        if (expirationSeconds < 1) {
            throw new IllegalStateException("JWT_EXPIRATION_SECONDS debe ser mayor que cero");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(UserDetails user) {
        try {
            long now = Instant.now().getEpochSecond();
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", user.getUsername());
            claims.put("iat", now);
            claims.put("exp", now + expirationSeconds);
            claims.put("roles", user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList());

            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            String header = encoder.encodeToString(HEADER.getBytes(StandardCharsets.UTF_8));
            String payload = encoder.encodeToString(objectMapper.writeValueAsBytes(claims));
            String content = header + "." + payload;
            return content + "." + encoder.encodeToString(sign(content));
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo crear el token", exception);
        }
    }

    public TokenClaims parseAndValidate(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3) {
                return null;
            }

            Base64.Decoder decoder = Base64.getUrlDecoder();
            Map<String, Object> header = objectMapper.readValue(decoder.decode(parts[0]), CLAIMS_TYPE);
            if (!"HS256".equals(header.get("alg")) || !"JWT".equals(header.get("typ"))) {
                return null;
            }

            byte[] expectedSignature = sign(parts[0] + "." + parts[1]);
            byte[] actualSignature = decoder.decode(parts[2]);
            if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
                return null;
            }

            Map<String, Object> claims = objectMapper.readValue(decoder.decode(parts[1]), CLAIMS_TYPE);
            Object subject = claims.get("sub");
            Object expiresAt = claims.get("exp");
            if (!(subject instanceof String username) || !(expiresAt instanceof Number expiry)
                    || expiry.longValue() <= Instant.now().getEpochSecond()) {
                return null;
            }

            return new TokenClaims(username);
        } catch (Exception exception) {
            return null;
        }
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private byte[] sign(String value) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(new SecretKeySpec(secret, HMAC_SHA256));
        return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }

    public record TokenClaims(String username) {}
}
