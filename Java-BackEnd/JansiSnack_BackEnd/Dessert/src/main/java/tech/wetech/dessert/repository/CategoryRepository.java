package tech.wetech.dessert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.api.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
