package com.fiap.sishotel.repository;

import com.fiap.sishotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    boolean existsByNumber(Integer number);

    @Query("""
        SELECT r FROM Room r
        WHERE r.status = com.fiap.sishotel.model.RoomStatus.ATIVO
          AND r.id NOT IN (
            SELECT res.room.id FROM Reservation res
            WHERE res.status NOT IN (com.fiap.sishotel.model.ReservationStatus.CANCELED)
              AND res.checkinExpected < :checkoutExpected
              AND res.checkoutExpected > :checkinExpected
          )
    """)
    List<Room> findAvailableRooms(@Param("checkinExpected") LocalDate checkinExpected,
                                  @Param("checkoutExpected") LocalDate checkoutExpected);
}
