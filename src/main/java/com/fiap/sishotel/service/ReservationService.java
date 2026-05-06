package com.fiap.sishotel.service;

import com.fiap.sishotel.dto.reservation.ReservationRequestDTO;
import com.fiap.sishotel.dto.reservation.ReservationResponseDTO;
import com.fiap.sishotel.exception.*;
import com.fiap.sishotel.mapper.ReservationMapper;
import com.fiap.sishotel.model.*;
import com.fiap.sishotel.repository.GuestRepository;
import com.fiap.sishotel.repository.ReservationRepository;
import com.fiap.sishotel.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              GuestRepository guestRepository,
                              RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
    }

    public List<ReservationResponseDTO> getAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationMapper::toResponseDTO)
                .toList();
    }

    public ReservationResponseDTO getById(String id) {
        return reservationRepository.findById(id)
                .map(ReservationMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada para o id: " + id));
    }

    public List<ReservationResponseDTO> getByGuest(String guestId) {
        if (!guestRepository.existsById(guestId)) {
            throw new ResourceNotFoundException("Hóspede não encontrado para o id: " + guestId);
        }
        return reservationRepository.findByGuestId(guestId).stream()
                .map(ReservationMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ReservationResponseDTO create(ReservationRequestDTO dto) {
        if (!dto.checkoutExpected().isAfter(dto.checkinExpected())) {
            throw new InvalidDateRangeException(
                    "A data de checkout previsto deve ser posterior à data de checkin previsto");
        }

        Guest guest = guestRepository.findById(dto.guestId())
                .orElseThrow(() -> new ResourceNotFoundException("Hóspede não encontrado para o id: " + dto.guestId()));

        Room room = roomRepository.findById(dto.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado para o id: " + dto.roomId()));

        if (room.getStatus() == RoomStatus.INATIVO) {
            throw new InvalidDateRangeException("Não é permitido reservar quarto inativo");
        }

        if (dto.numGuests() > room.getCapacity()) {
            throw new CapacityExceededException(
                    String.format("Número de hóspedes (%d) excede a capacidade do quarto (%d)",
                            dto.numGuests(), room.getCapacity()));
        }

        if (reservationRepository.hasOverlap(room.getId(), dto.checkinExpected(), dto.checkoutExpected(), null)) {
            throw new RoomUnavailableException(
                    "Quarto " + room.getNumber() + " indisponível no período solicitado");
        }

        long nights = ChronoUnit.DAYS.between(dto.checkinExpected(), dto.checkoutExpected());
        BigDecimal estimated = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .room(room)
                .checkinExpected(dto.checkinExpected())
                .checkoutExpected(dto.checkoutExpected())
                .numGuests(dto.numGuests())
                .estimatedAmount(estimated)
                .build();

        return ReservationMapper.toResponseDTO(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponseDTO checkIn(String id) {
        Reservation reservation = findById(id);

        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new InvalidReservationStateException(
                    "Check-in inválido. Status atual: " + reservation.getStatus() +
                    ". Apenas reservas com status CREATED podem realizar check-in.");
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(reservation.getCheckinExpected())) {
            throw new InvalidReservationStateException(
                    "Check-in permitido somente a partir de " + reservation.getCheckinExpected() +
                    ". Data atual: " + today);
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckinAt(LocalDateTime.now());

        return ReservationMapper.toResponseDTO(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponseDTO checkOut(String id) {
        Reservation reservation = findById(id);

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new InvalidReservationStateException(
                    "Check-out inválido. Status atual: " + reservation.getStatus() +
                    ". Apenas reservas com status CHECKED_IN podem realizar check-out.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate checkinDate = reservation.getCheckinAt().toLocalDate();
        LocalDate checkoutDate = now.toLocalDate();

        long nights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        long effectiveNights = Math.max(1, nights);

        BigDecimal finalAmount = reservation.getRoom().getPricePerNight()
                .multiply(BigDecimal.valueOf(effectiveNights));

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservation.setCheckoutAt(now);
        reservation.setFinalAmount(finalAmount);

        return ReservationMapper.toResponseDTO(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponseDTO cancel(String id) {
        Reservation reservation = findById(id);

        if (reservation.getStatus() != ReservationStatus.CREATED) {
            throw new InvalidReservationStateException(
                    "Cancelamento inválido. Status atual: " + reservation.getStatus() +
                    ". Apenas reservas com status CREATED podem ser canceladas.");
        }

        reservation.setStatus(ReservationStatus.CANCELED);

        return ReservationMapper.toResponseDTO(reservationRepository.save(reservation));
    }

    private Reservation findById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada para o id: " + id));
    }
}
