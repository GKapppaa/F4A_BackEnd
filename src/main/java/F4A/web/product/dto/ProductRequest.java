package F4A.web.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductRequest(
        @NotBlank @Size(max = 160) String nombre,
        @Size(max = 1000) String descripcion,
        @NotNull Long categoriaId,
        @NotNull Long plataformaId,
        @NotNull Long regionActivacionId,
        List<@NotBlank @Size(max = 1000) String> imagenes
) {
}
