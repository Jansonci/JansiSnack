package tech.wetech.order.producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.wetech.order.producer.service.InventoryService;

/**
 * @auther zzyy
 * @create 2022-10-12 17:05
 */
@RestController
public class InventoryController
{
    @Autowired
    private InventoryService inventoryService;

    @GetMapping(value = "/inventory/sale")
    public String sale()
    {
        return inventoryService.sale();
    }
}



