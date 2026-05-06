package com.fiap.sishotel.service;

import com.fiap.sishotel.dto.guest.GuestRequestDTO;
import com.fiap.sishotel.dto.guest.GuestResponseDTO;
import com.fiap.sishotel.exception.DuplicateGuestException;
import com.fiap.sishotel.exception.GuestHasReservationsException;
import com.fiap.sishotel.exception.ResourceNotFoundException;
import com.fiap.sishotel.mapper.GuestMapper;
import com.fiap.sishotel.model.Guest;
import com.fiap.sishotel.repository.GuestRepository;
import com.fiap.sishotel.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {

    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    public GuestService(GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<GuestResponseDTO> getAll() {
        return guestRepository.findAll().stream()
                .map(GuestMapper::toResponseDTO)
                .toList();
    }

    public GuestResponseDTO getById(String id) {
        return guestRepository.findById(id)
                .map(GuestMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Hóspede não encontrado para o id: " + id));
    }

    @Transactional
    public GuestResponseDTO create(GuestRequestDTO dto) {
        if (guestRepository.existsByDocument(dto.document())) {
            throw new DuplicateGuestException("Já existe hóspede cadastrado com o documento: " + dto.document());
        }
        if (guestRepository.existsByEmail(dto.email())) {
            throw new DuplicateGuestException("Já existe hóspede cadastrado com o e-mail: " + dto.email());
        }
        Guest saved = guestRepository.save(GuestMapper.toEntity(dto));
        return GuestMapper.toResponseDTO(saved);
    }

    @Transactional
    public GuestResponseDTO update(String id, GuestRequestDTO dto) {
        Guest existing = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóspede não encontrado para o id: " + id));

        if (!existing.getDocument().equals(dto.document()) && guestRepository.existsByDocument(dto.document())) {
            throw new DuplicateGuestException("Documento já utilizado por outro hóspede: " + dto.document());
        }
        if (!existing.getEmail().equals(dto.email()) && guestRepository.existsByEmail(dto.email())) {
            throw new DuplicateGuestException("E-mail já utilizado por outro hóspede: " + dto.email());
        }

        existing.setFullName(dto.fullName());
        existing.setDocument(dto.document());
        existing.setEmail(dto.email());
        existing.setPhone(dto.phone());

        return GuestMapper.toResponseDTO(guestRepository.save(existing));
    }

    @Transactional
    public void delete(String id) {
        if (!guestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hóspede não encontrado para o id: " + id);
        }
        if (reservationRepository.existsByGuestId(id)) {
            throw new GuestHasReservationsException("Não é possível remover hóspede com reservas vinculadas");
        }
        guestRepository.deleteById(id);
    }
}
