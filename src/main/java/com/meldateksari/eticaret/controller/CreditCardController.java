package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.dto.CreditCardDto;
import com.meldateksari.eticaret.model.CreditCard;
import com.meldateksari.eticaret.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService service;

    @PostMapping
    public ResponseEntity<CreditCard> addCard(@RequestBody CreditCardDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.principal.id") // bu çalışıyorsa sorun yok
    public ResponseEntity<List<CreditCardDto>> getCards(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

