package F4A.web.product.service;

import F4A.web.category.model.Category;
import F4A.web.category.repository.CategoryRepository;
import F4A.web.product.model.Product;
import F4A.web.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"
                ));
    }

    public Product create(Product product, Long categoriaId) {
        product.setCategoria(findCategory(categoriaId));
        return productRepository.save(product);
    }

    public Product update(Long id, Product updatedData, Long categoriaId) {
        Product product = findById(id);
        product.setNombre(updatedData.getNombre());
        product.setPrecio(updatedData.getPrecio());
        product.setStock(updatedData.getStock());
        product.setImagen(updatedData.getImagen());
        product.setDescripcion(updatedData.getDescripcion());
        product.setEsOferta(updatedData.isEsOferta());
        product.setCategoria(findCategory(categoriaId));

        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.delete(findById(id));
    }

    private Category findCategory(Long categoriaId) {
        return categoryRepository.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada"
                ));
    }
}