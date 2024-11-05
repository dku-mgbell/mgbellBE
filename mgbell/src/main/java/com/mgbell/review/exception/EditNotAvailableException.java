package com.mgbell.review.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class EditNotAvailableException extends CustomException {
  public EditNotAvailableException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "EDIT_NOT_AVAILABLE");
  }
}
