package com.fiap.sishotel.exception;

public class RoomHasReservationsException extends RuntimeException {
    public RoomHasReservationsException(String message) {
        super(message);
    }
}
