package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.ProductImage;
import java.util.List;

public interface ProductImageService {
    ProductImage save(ProductImage productImage);
    List<ProductImage> findByProductId(Long productId);
    void deleteById(Long id);
    void deleteByProductId(Long productId);
}
