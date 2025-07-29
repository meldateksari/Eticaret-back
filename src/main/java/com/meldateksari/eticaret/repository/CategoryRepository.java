package com.meldateksari.eticaret.repository;

import com.meldateksari.eticaret.dto.CategoryDto;
import com.meldateksari.eticaret.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull(); // Üst kategoriler
    List<Category> findByParentId(Long parentId); // Alt kategoriler

    List<Category> findByNameIn(List<String> genderCategoryNames);
}
