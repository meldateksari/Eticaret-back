package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.UserProductInteraction;
import com.meldateksari.eticaret.repository.UserProductInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProductInteractionService {

    private final UserProductInteractionRepository interactionRepository;

    public UserProductInteraction save(UserProductInteraction interaction) {
        return interactionRepository.save(interaction);
    }

    public List<UserProductInteraction> getInteractionsByUserId(Long userId) {
        return interactionRepository.findByUserId(userId);
    }

    public List<UserProductInteraction> getInteractionsByProductId(Long productId) {
        return interactionRepository.findByProductId(productId);
    }

    public void deleteById(Long id) {
        interactionRepository.deleteById(id);
    }
    public UserProductInteraction saveInteraction(UserProductInteraction interaction) {
        return interactionRepository.save(interaction);
    }

}
