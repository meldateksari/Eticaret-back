package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.ReviewDto;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.Review;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.ProductRepository;
import com.meldateksari.eticaret.repository.ReviewRepository;
import com.meldateksari.eticaret.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public ReviewDto createReview(ReviewDto reviewDto) {
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Product product = productRepository.findById(reviewDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(reviewDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reviewDto.getUserId()));


        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);

        return convertToDto(savedReview);
    }

    public List<ReviewDto> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // DTO'ya dönüştürme işlemi
    private ReviewDto convertToDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
