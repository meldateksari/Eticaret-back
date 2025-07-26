package com.meldateksari.eticaret.repository;

import com.meldateksari.eticaret.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
