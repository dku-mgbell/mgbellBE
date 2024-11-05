package com.mgbell.global.auth.jwt.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends CustomException {
  public InvalidTokenException() {
    super(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
  }
}
