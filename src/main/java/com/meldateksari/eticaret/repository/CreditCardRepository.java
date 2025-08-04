package com.meldateksari.eticaret.repository;

import com.meldateksari.eticaret.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    List<CreditCard> findByUserId(Long userId);
}

