package com.fiap.sishotel.exception;

public class GuestHasReservationsException extends RuntimeException {
    public GuestHasReservationsException(String message) {
        super(message);
    }
}
