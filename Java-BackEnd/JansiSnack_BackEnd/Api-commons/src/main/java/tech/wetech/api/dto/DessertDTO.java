package tech.wetech.api.dto;

import java.util.List;

public record DessertDTO(Long id,
                         String name,
                         String categoryName,
                         String lifestyle,
                         String flavor,
                         Double price,
                         String difficulty,
                         Integer prepTime,
                         Integer cookTime,
                         String nutritionInfo,
                         String occasion,
                         String storageInfo,
                         Integer shelfLife,
                         Double rating,
                         String imageUrl,
                         String region,
                         List<String> ingredients
                         ) {

}
