package F4A.web.role.repository;

import F4A.web.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    java.util.Optional<Role> findByNombreIgnoreCase(String nombre);
}
