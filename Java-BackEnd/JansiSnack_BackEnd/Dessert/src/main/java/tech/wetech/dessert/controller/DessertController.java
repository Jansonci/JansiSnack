package tech.wetech.dessert.controller;

import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wetech.api.dto.DessertDTO;
import tech.wetech.api.model.Category;
import tech.wetech.api.model.Lifestyle;
import tech.wetech.dessert.service.DessertService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jansonci
 */
@RestController
@RequestMapping("/desserts")
public class DessertController {

  @Resource
  public DessertService dessertService;

  @Cacheable(value = "dessert", key = "#dessertId", unless = "#result == null")
  @GetMapping("/{dessertId}")
  public ResponseEntity<DessertDTO> findDessertByName(@PathVariable Long dessertId) {
    return ResponseEntity.ok(dessertService.findDessertByNameWithRedis(dessertId));
  }

  @GetMapping("/all")
  public ResponseEntity<List<DessertDTO>> findAllDesserts() {
    return ResponseEntity.ok(dessertService.findAllDesserts());
  }

  @Cacheable(value = "desserts", key = "#dessertIds", unless = "#result == null")
  @GetMapping("/some")
  public ResponseEntity<Set<DessertDTO>> findSomeDesserts(@RequestBody Set<Long> dessertIds) {
    return ResponseEntity.ok(dessertService.findSomeDesserts(dessertIds));
  }

  @GetMapping("/allCategories")
  public ResponseEntity<List<Category>> findAllCategories() {
    return ResponseEntity.ok(dessertService.findAllCategories());
  }

  @GetMapping("/allLifestyles")
  public ResponseEntity<List<Lifestyle>> findAllLifestyles() {
    return ResponseEntity.ok(dessertService.findAllLifestyles());
  }

  @PostMapping("/sell")
  public ResponseEntity<Void> updateDessertsStorage(@RequestBody Map<Long, Integer> orderInfo) {
    dessertService.updateDessertsStorage(orderInfo);
    return ResponseEntity.ok().build();
  }
}
