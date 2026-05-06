package com.fiap.sishotel.repository;

import com.fiap.sishotel.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, String> {
    boolean existsByDocument(String document);
    boolean existsByEmail(String email);
}
