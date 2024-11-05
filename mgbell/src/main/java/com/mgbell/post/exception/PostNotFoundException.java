package com.mgbell.post.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends CustomException {
  public PostNotFoundException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "POST_NOT_FOUND");
  }
}
