package F4A.web.category.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import F4A.web.category.model.Category;
import F4A.web.category.repository.CategoryRepository;

@Service 
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository){
        this.repository = repository;
    }

    public List<Category> findAll(){
        return repository.findAll();
    }

    public Category findById(Long id){
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria no encontrada"));
    }

    public Category create(Category category){
        return repository.save(category);
    }

    public Category update(Long id, Category updatedData){
        Category category = findById(id);
        category.setNombre(updatedData.getNombre());
        category.setDescripcion(updatedData.getDescripcion());
        return repository.save(category);
    }

    public void delete(Long id){
        Category category = findById(id);
        repository.delete(category);
    }

}
