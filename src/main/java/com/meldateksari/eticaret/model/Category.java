package com.meldateksari.eticaret.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Kategori - Üst Kategori ilişkisi (parent)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_categories_parent"))
    @JsonIgnore
    private Category parent;

    // Kategori - Alt Kategoriler ilişkisi (children)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Category> children = new HashSet<>();

    // YENİ EKLENECEK KISIM: Many-to-Many ilişkisi için
    // Bir cinsiyet kategorisine ait olan ürünleri tutacak
    @ManyToMany(mappedBy = "genderCategories") // Product sınıfındaki field adını belirtiyoruz
    @JsonIgnore // JSON serileştirmesinde sonsuz döngüyü engellemek için
    private Set<Product> products = new HashSet<>();
}