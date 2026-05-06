package com.fiap.sishotel.repository;

import com.fiap.sishotel.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    List<Reservation> findByGuestId(String guestId);
    boolean existsByGuestId(String guestId);

    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.status <> com.fiap.sishotel.model.ReservationStatus.CANCELED
          AND r.checkinExpected < :checkoutExpected
          AND r.checkoutExpected > :checkinExpected
          AND (:excludeId IS NULL OR r.id <> :excludeId)
    """)
    boolean hasOverlap(@Param("roomId") String roomId,
                       @Param("checkinExpected") LocalDate checkinExpected,
                       @Param("checkoutExpected") LocalDate checkoutExpected,
                       @Param("excludeId") String excludeId);
}
