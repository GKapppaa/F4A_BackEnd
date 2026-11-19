package F4A.web.product.service;

import F4A.web.category.model.Category;
import F4A.web.category.repository.CategoryRepository;
import F4A.web.platform.model.Platform;
import F4A.web.platform.repository.PlatformRepository;
import F4A.web.product.dto.ProductRequest;
import F4A.web.product.model.Product;
import F4A.web.product.repository.ProductRepository;
import F4A.web.region.model.ActivationRegion;
import F4A.web.region.repository.ActivationRegionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PlatformRepository platformRepository;
    private final ActivationRegionRepository activationRegionRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            PlatformRepository platformRepository,
            ActivationRegionRepository activationRegionRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.platformRepository = platformRepository;
        this.activationRegionRepository = activationRegionRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"
                ));
    }

    @Transactional
    public Product create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        applyRequest(product, request);
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(findById(id));
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setNombre(request.nombre());
        product.setDescripcion(request.descripcion());
        product.setCategoria(findCategory(request.categoriaId()));
        product.setPlataforma(findPlatform(request.plataformaId()));
        product.setRegionActivacion(findActivationRegion(request.regionActivacionId()));
        product.replaceImagenes(request.imagenes());
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada"
                ));
    }

    private Platform findPlatform(Long id) {
        return platformRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Plataforma no encontrada"
                ));
    }

    private ActivationRegion findActivationRegion(Long id) {
        return activationRegionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Región de activación no encontrada"
                ));
    }
}
