package com.isedol_clip_backend.exception;

public class AlreadyExistedDataException extends Exception {
    public AlreadyExistedDataException() {
    }

    public AlreadyExistedDataException(String message) {
        super(message);
    }
}
