package com.mgbell.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("USER_NOT_FOUND");
        // Todo 커스텀 에러 메시지
    }
}
