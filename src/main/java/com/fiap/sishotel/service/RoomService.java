package com.fiap.sishotel.service;

import com.fiap.sishotel.dto.room.RoomRequestDTO;
import com.fiap.sishotel.dto.room.RoomResponseDTO;
import com.fiap.sishotel.exception.ResourceNotFoundException;
import com.fiap.sishotel.exception.RoomHasReservationsException;
import com.fiap.sishotel.exception.RoomUnavailableException;
import com.fiap.sishotel.mapper.RoomMapper;
import com.fiap.sishotel.model.Room;
import com.fiap.sishotel.model.RoomStatus;
import com.fiap.sishotel.repository.ReservationRepository;
import com.fiap.sishotel.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomResponseDTO> getAll() {
        return roomRepository.findAll().stream()
                .map(RoomMapper::toResponseDTO)
                .toList();
    }

    public RoomResponseDTO getById(String id) {
        return roomRepository.findById(id)
                .map(RoomMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado para o id: " + id));
    }

    public List<RoomResponseDTO> getAvailable(LocalDate checkin, LocalDate checkout) {
        if (!checkout.isAfter(checkin)) {
            throw new com.fiap.sishotel.exception.InvalidDateRangeException(
                    "Data de checkout deve ser posterior à data de checkin");
        }
        return roomRepository.findAvailableRooms(checkin, checkout).stream()
                .map(RoomMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public RoomResponseDTO create(RoomRequestDTO dto) {
        if (roomRepository.existsByNumber(dto.number())) {
            throw new RoomUnavailableException("Já existe um quarto com o número: " + dto.number());
        }
        Room saved = roomRepository.save(RoomMapper.toEntity(dto));
        return RoomMapper.toResponseDTO(saved);
    }

    @Transactional
    public RoomResponseDTO update(String id, RoomRequestDTO dto) {
        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado para o id: " + id));

        if (!existing.getNumber().equals(dto.number()) && roomRepository.existsByNumber(dto.number())) {
            throw new RoomUnavailableException("Já existe um quarto com o número: " + dto.number());
        }

        existing.setNumber(dto.number());
        existing.setType(dto.type());
        existing.setCapacity(dto.capacity());
        existing.setPricePerNight(dto.pricePerNight());

        return RoomMapper.toResponseDTO(roomRepository.save(existing));
    }


    @Transactional
    public RoomResponseDTO deactivate(String id) {
        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado para o id: " + id));

        boolean hasActiveReservations = reservationRepository.hasOverlap(
                id, LocalDate.now(), LocalDate.now().plusYears(10), null);

        if (hasActiveReservations) {
            throw new RoomHasReservationsException(
                    "Não é possível desativar quarto com reservas ativas ou futuras. " +
                    "Cancele as reservas antes de desativar o quarto.");
        }

        existing.setStatus(RoomStatus.INATIVO);
        return RoomMapper.toResponseDTO(roomRepository.save(existing));
    }
}
