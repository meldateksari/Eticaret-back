package com.meldateksari.eticaret.dto;

// package com.meldateksari.eticaret.dto; // Kendi paket yolunuza göre ayarlayın

import com.meldateksari.eticaret.model.Category;

public class CategoryMapper {

    // Category objesini CategoryDto objesine dönüştüren static metot
    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        // Eğer üst kategori varsa parentId'yi ayarla
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }
        // Eğer alt kategorileri de DTO'ya dönüştürmek isterseniz buraya ekleme yapabilirsiniz
        // if (category.getChildren() != null && !category.getChildren().isEmpty()) {
        //     dto.setChildren(category.getChildren().stream()
        //                               .map(CategoryMapper::toCategoryDto) // Alt kategoriler için recursive çağrı
        //                               .collect(Collectors.toList()));
        // }
        return dto;
    }

    // CategoryDto objesini Category objesine dönüştüren static metot (genellikle update/create için)
    public static Category toCategoryDto(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        // ID'yi sadece update işlemlerinde set etmek isteyebilirsiniz, create'de DB otomatik verir
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        // Parent ilişkisi Service katmanında Repository ile yönetilmelidir (findById ile çekilerek)
        return category;
    }
}