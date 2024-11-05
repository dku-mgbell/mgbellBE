package com.mgbell.store.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class AlreadyHasStoreException extends CustomException {
  public AlreadyHasStoreException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "ALREADY_HAS_STORE");
  }
}
