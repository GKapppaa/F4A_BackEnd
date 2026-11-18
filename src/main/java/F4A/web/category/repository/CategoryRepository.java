package F4A.web.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import F4A.web.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
