package tech.wetech.dessert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.api.model.Lifestyle;

/**
 * @author Jansonci
 */
@Repository
public interface LifestyleRepository extends JpaRepository<Lifestyle, Long> {


}
