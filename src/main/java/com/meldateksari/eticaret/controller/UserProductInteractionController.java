package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.model.UserProductInteraction;
import com.meldateksari.eticaret.service.UserProductInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class UserProductInteractionController {

    private final UserProductInteractionService interactionService;

    @PostMapping
    public ResponseEntity<UserProductInteraction> saveInteraction(@RequestBody UserProductInteraction interaction) {
        return ResponseEntity.ok(interactionService.saveInteraction(interaction));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserProductInteraction>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(interactionService.getInteractionsByUserId(userId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<UserProductInteraction>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(interactionService.getInteractionsByProductId(productId));
    }
}
