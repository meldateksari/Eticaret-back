package com.meldateksari.eticaret.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_features")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    private String material;

    private String color;

    private String size;

    @Column(name = "technical_specs", columnDefinition = "json")
    private String technicalSpecs; // JSON formatında string olarak saklanır
}
