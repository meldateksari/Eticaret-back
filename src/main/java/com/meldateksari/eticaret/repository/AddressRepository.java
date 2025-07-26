package com.meldateksari.eticaret.repository;

import com.meldateksari.eticaret.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Kullanıcıya ait adresleri getirmek için
    List<Address> findByUserId(Long userId);

    // Kullanıcının varsayılan adresini getirmek için
    Address findByUserIdAndIsDefaultTrue(Long userId);
}
