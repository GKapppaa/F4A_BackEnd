package F4A.web.user.repository;

import F4A.web.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "rol")
    Optional<User> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
}
