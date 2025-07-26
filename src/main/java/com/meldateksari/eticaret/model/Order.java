package com.meldateksari.eticaret.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kullanıcı silinemezse ON DELETE RESTRICT davranışı zaten varsayılandır (cascade yok)
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;
    //Bir teslimat adresi silinmek istendiğinde, eğer bu adres bir siparişte kullanılmışsa, silme işlemi engellenir.

    @ManyToOne
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;
    //Bir fatura adresi silinmek istendiğinde, eğer bu adres bir siparişte kullanılmışsa, silme işlemi engellenir.

    @Column(nullable = false, length = 50)
    private String status; // örn: PENDING, SHIPPED, DELIVERED

    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus; // örn: PAID, UNPAID, REFUNDED

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

}
