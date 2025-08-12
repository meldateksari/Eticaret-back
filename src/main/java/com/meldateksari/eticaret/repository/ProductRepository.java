package com.meldateksari.eticaret.repository;

import com.meldateksari.eticaret.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
    SELECT DISTINCT p FROM Product p
    LEFT JOIN FETCH p.genderCategories gc
    LEFT JOIN FETCH p.category c
    LEFT JOIN FETCH p.images i
    WHERE (:genderCategoryIds IS NULL OR gc.id IN :genderCategoryIds)
      AND (:categoryId IS NULL OR c.id = :categoryId)
      AND (:isActive IS NULL OR p.isActive = :isActive)
    ORDER BY p.createdAt DESC, p.id DESC
    """)
    List<Product> findWithFilters(
            @Param("genderCategoryIds") List<Long> genderCategoryIds,
            @Param("categoryId") Long categoryId,
            @Param("isActive") Boolean isActive
    );

    List<Product> findTop3ByOrderByCreatedAtDesc();
    boolean existsBySlug(String slug);


}
