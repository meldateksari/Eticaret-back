package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.CategoryMapper;
import com.meldateksari.eticaret.dto.CategoryDto;
import com.meldateksari.eticaret.model.Category;
import com.meldateksari.eticaret.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return CategoryMapper.toCategoryDto(category);
    }

    public List<CategoryDto> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getSubCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setSlug(categoryDto.getSlug());
        category.setDescription(categoryDto.getDescription());
        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        }
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(savedCategory);
    }

    public CategoryDto updateCategory(Long id, CategoryDto updatedCategoryDto) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(updatedCategoryDto.getName());
                    category.setSlug(updatedCategoryDto.getSlug());
                    category.setDescription(updatedCategoryDto.getDescription());
                    if (updatedCategoryDto.getParentId() != null) {
                        Category parent = categoryRepository.findById(updatedCategoryDto.getParentId())
                                .orElseThrow(() -> new RuntimeException("Parent category not found"));
                        category.setParent(parent);
                    } else {
                        category.setParent(null);
                    }
                    Category savedCategory = categoryRepository.save(category);
                    return CategoryMapper.toCategoryDto(savedCategory);
                })
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }



    /**
     * Sadece cinsiyet kategorilerini (Kadın, Erkek, Unisex) döndüren metot.
     * Bu metot, frontend'den gelen 'type=gender' parametresiyle kullanılabilir.
     */
    public List<CategoryDto> getGenderCategories() {
        List<String> genderCategoryNames = Arrays.asList("woman", "man");
        return categoryRepository.findByNameIn(genderCategoryNames).stream()
                .map(CategoryMapper::toCategoryDto) // Category'den CategoryDto'ya dönüştür
                .collect(Collectors.toList());
    }

    /**
     * Tüm kategorileri veya belirli türdeki kategorileri getirmek için genelleştirilmiş metot.
     * Bu metot, CategoryController'daki getAllCategories methodu tarafından kullanılabilir.
     * @param type "gender" ise cinsiyet kategorilerini döndürür. Null veya boş ise tüm kategorileri döndürür.
     */
    public List<CategoryDto> getAllCategories(String type) {
        if ("gender".equalsIgnoreCase(type)) {
            return getGenderCategories();
        }
        return getAllCategories(); // Varsayılan olarak tüm kategorileri döndür
    }
}

