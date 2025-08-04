// src/main/java/com/meldateksari/eticaret/service/UserProductInteractionService.java

package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.UserProductInteraction;
import com.meldateksari.eticaret.repository.ProductRepository;
import com.meldateksari.eticaret.repository.UserProductInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProductInteractionService {

    private final UserProductInteractionRepository interactionRepository;
    @Autowired
    private ProductRepository productRepository;


    /**
     * Kullanıcı etkileşimlerini asenkron (arka planda) olarak kaydeder.
     * Bu metot, ana thread'i bloke etmez ve uygulamanın performansını artırır.
     */
    @Async
    public void logInteraction(UserProductInteraction newInteraction) {
        Optional<UserProductInteraction> existingInteractionOpt =
                interactionRepository.findByUserIdAndProductIdAndInteractionType(
                        newInteraction.getUser().getId(),
                        newInteraction.getProduct().getId(),
                        newInteraction.getInteractionType()
                );

        if (existingInteractionOpt.isPresent()) {
            UserProductInteraction existingInteraction = existingInteractionOpt.get();
            existingInteraction.setTimestamp(LocalDateTime.now());
            interactionRepository.save(existingInteraction);
        } else {
            interactionRepository.save(newInteraction);
        }
    }

    public List<Product> recommendProducts(Long userId) {
        // 1. Kullanıcının etkileşim geçmişini al
        List<UserProductInteraction> interactions = interactionRepository.findByUserIdOrderByTimestampDesc(userId);
        return interactions.stream().map(m -> m.getProduct()).toList();
        // 2. En çok baktığı kategoriyi/markanın ürünlerini bul
//        Map<String, Long> brandCount = interactions.stream()
//                .collect(Collectors.groupingBy(i -> i.getProduct().getBrand(), Collectors.counting()));
//
//        String mostViewedBrand = brandCount.entrySet().stream()
//                .max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey)
//                .orElse(null);

        // 3. O markaya ait başka ürünleri öner
//        return productRepository.findByIsActiveTrue(PageRequest.of(0, 10));

    }

}