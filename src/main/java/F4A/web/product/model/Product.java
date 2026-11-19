package F4A.web.product.model;

import F4A.web.category.model.Category;
import F4A.web.platform.model.Platform;
import F4A.web.region.model.ActivationRegion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Category categoria;

    // Se dejan nullable temporalmente para conservar productos antiguos
    // mientras se prepara la migración de plataforma y región.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plataforma_id")
    private Platform plataforma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_activacion_id")
    private ActivationRegion regionActivacion;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> imagenes = new ArrayList<>();

    public Product() {
        // Requerido por JPA.
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Category getCategoria() {
        return categoria;
    }

    public void setCategoria(Category categoria) {
        this.categoria = categoria;
    }

    public Platform getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(Platform plataforma) {
        this.plataforma = plataforma;
    }

    public ActivationRegion getRegionActivacion() {
        return regionActivacion;
    }

    public void setRegionActivacion(ActivationRegion regionActivacion) {
        this.regionActivacion = regionActivacion;
    }

    public List<ProductImage> getImagenes() {
        return imagenes;
    }

    public void replaceImagenes(List<String> urls) {
        imagenes.clear();
        if (urls != null) {
            urls.stream()
                    .filter(url -> url != null && !url.isBlank())
                    .forEach(url -> imagenes.add(new ProductImage(url, this)));
        }
    }
}
