package com.meldateksari.eticaret.repository;

import com.meldateksari.eticaret.model.UserProductInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProductInteractionRepository extends JpaRepository<UserProductInteraction, Long> {
    List<UserProductInteraction> findByUserIdOrderByTimestampDesc(Long userId);
    List<UserProductInteraction> findByProductId(Long productId);
}
