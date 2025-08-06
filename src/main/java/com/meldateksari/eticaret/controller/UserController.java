package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.auth.dto.UpdatePasswordRequestDto;
import com.meldateksari.eticaret.auth.enums.Role;
import com.meldateksari.eticaret.dto.ProductResponseDto;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.service.UserProductInteractionService;
import com.meldateksari.eticaret.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200", methods = { RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST })
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProductInteractionService interactionService;


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updateUserPassword(@PathVariable Long id, @RequestBody UpdatePasswordRequestDto dto) {
        try {
            userService.updatePassword(id, dto);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{userId}/roles/{role}")
    public ResponseEntity<User> assignRoleToUser(@PathVariable Long userId, @PathVariable Role role) {
        return ResponseEntity.ok(userService.assignRoleToUser(userId, role));
    }
    @GetMapping("/{userId}/recommended-products")
    public ResponseEntity<List<ProductResponseDto>> getRecommendedProducts(@PathVariable Long userId) {
        List<Product> recommended = interactionService.recommendProducts(userId);
        List<ProductResponseDto> response = recommended.stream().map(product -> ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build()).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("file") MultipartFile file,
                                                                  @AuthenticationPrincipal User user) throws IOException {

        // 1. Fiziksel olarak kaydet (isteğe bağlı, veri kaybı olmasın)
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("C:\\uploads\\profile-images\\");
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 2. Kullanıcıya dosya yolu kaydet (DB'de saklansın)
        String imageUrl = "C:\\uploads\\profile-images\\" + fileName;
        user.setProfileImageUrl(imageUrl);
        userService.getUserRepository().save(user);

        // 3. Base64 oluştur
        String contentType = file.getContentType(); // image/jpeg, image/png vs.
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);

        String base64Image = "data:" + contentType + ";base64," + base64;

        // 4. Geri dön
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", base64Image); // frontend doğrudan gösterir
        return ResponseEntity.ok(response);
    }


}
