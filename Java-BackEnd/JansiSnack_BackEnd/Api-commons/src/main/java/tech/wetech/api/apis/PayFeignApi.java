package tech.wetech.api.apis;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.wetech.api.dto.DessertDTO;

import java.util.Map;
import java.util.Set;

/**
 * @author Jansonci
 * @create 2023-12-25 11:27
 */
@FeignClient(value = "dessert-info-service")
public interface PayFeignApi
{
  @GetMapping(value = "/desserts/{dessertId}")
  public ResponseEntity<DessertDTO> findDessertByName(@PathVariable("dessertId") Long dessertId);

  @PostMapping(value = "/desserts/sell")
  public ResponseEntity<Void> updateDessertsStorage(@RequestBody Map<Long, Integer> orderInfo);

  @GetMapping(value = "/desserts/some")
  public ResponseEntity<Set<DessertDTO>> findSomeDesserts(@RequestBody Set<Long> dessertIds);

}


