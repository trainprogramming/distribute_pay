package com.appchemist.distribute_pay.exception;


public class NotExistTokenException extends RuntimeException {
    public NotExistTokenException(String token, String roomId) {
        super(String.format("Not Exist Token : token(%s), roomId(%s)", token, roomId));
    }
}