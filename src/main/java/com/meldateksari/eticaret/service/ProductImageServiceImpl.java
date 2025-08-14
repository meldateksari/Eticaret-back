package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.ProductImageDto;
import com.meldateksari.eticaret.dto.ProductResponseDto;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.ProductImage;
import com.meldateksari.eticaret.repository.ProductImageRepository;
import com.meldateksari.eticaret.repository.ProductRepository;
import com.meldateksari.eticaret.service.ProductImageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductService productService;

    @Override
    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public List<ProductImageDto> findByProductId(Long productId) {
        List<ProductImage> imageList = productImageRepository.findByProductId(productId);
        List<ProductImageDto> dtoList = imageList.stream().map(m -> {
            return new ProductImageDto(
                    m.getId(),
                    m.getImageUrl(),
                    m.getIsThumbnail(),
                    m.getSortOrder()
            );
        }).toList();
        for (ProductImageDto productImage : dtoList) {
            String relativePath = productImage.getImageUrl();
            if (relativePath != null && !relativePath.startsWith("http")) {
                String fullPath = relativePath.replace("/", "\\");
                File file = new File(fullPath);
                if (!file.exists()) {
                    continue;
                }
                try {
                    productImage.setImage(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return dtoList;
    }

    @Override
    public void deleteById(Long id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public void deleteByProductId(Long productId) {
        productImageRepository.deleteByProductId(productId);
    }



    @Transactional
    public ProductImageDto add(ProductImageDto dto) {
        if (dto.getImage() != null) {
            try {
                // Kaydedilecek yol
                Path path = Paths.get("C:\\uploads\\product-images\\", UUID.randomUUID().toString());
                Files.createDirectories(path.getParent()); // klasör yoksa oluştur

                // Byte dizisini dosyaya yaz
                Files.write(path, dto.getImage());
                dto.setImageUrl(path.toString());
                System.out.println("Resim kaydedildi: " + path.toAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                // Hata yönetimi
            }
        }


        ProductImage img = new ProductImage();
        img.setImageUrl(dto.getImageUrl());
        img.setIsThumbnail(Boolean.TRUE.equals(dto.getIsThumbnail()));
        img.setSortOrder(dto.getSortOrder());

        Long productId = dto.getProduct().getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        img.setProduct(product);

        ProductImage saved = productImageRepository.save(img);
        return convertToDto(saved);
    }

    private ProductImageDto convertToDto(ProductImage entity) {
        ProductImageDto dto = new ProductImageDto();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setIsThumbnail(entity.getIsThumbnail());
        dto.setSortOrder(entity.getSortOrder());

        // sadece id göndermek yeterli olabilir
        ProductResponseDto productDto = new ProductResponseDto();
        productDto.setId(entity.getProduct().getId());
        dto.setProduct(productDto);

        return dto;
    }
}
