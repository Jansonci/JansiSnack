package tech.wetech.dessert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.api.model.Dessert;

import java.util.Optional;

/**
 * @author Jansonci
 */
@Repository
public interface DessertRepository extends JpaRepository<Dessert, Long> {
 Optional<Dessert> findDessertByName(String name);

 Page<Dessert> findDessertsByCategoryOrderById(String category, Pageable pageable);
}
