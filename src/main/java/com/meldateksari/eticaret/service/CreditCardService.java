package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.CreditCardDto;
import com.meldateksari.eticaret.model.CreditCard;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.CreditCardRepository;
import com.meldateksari.eticaret.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;

    public CreditCard create(CreditCardDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        CreditCard card = CreditCard.builder()
                .cardNumber(dto.getCardNumber())
                .expiryDate(dto.getExpiryDate())
                .cvv(dto.getCvv())
                .user(user)
                .build();

        return creditCardRepository.save(card);
    }

    public List<CreditCardDto> getByUserId(Long userId) {
        return creditCardRepository.findByUserId(userId)
                .stream()
                .map(card -> new CreditCardDto(
                        card.getId(),
                        card.getCardNumber(),
                        card.getExpiryDate(),
                        card.getCvv(),
                        card.getUser().getId()
                ))

                .toList();
    }


    public void delete(Long id) {
        creditCardRepository.deleteById(id);
    }

}