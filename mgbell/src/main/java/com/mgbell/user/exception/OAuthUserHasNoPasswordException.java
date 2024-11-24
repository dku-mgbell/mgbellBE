package com.mgbell.user.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class OAuthUserHasNoPasswordException extends CustomException {
  public OAuthUserHasNoPasswordException() {
    super(HttpStatus.BAD_REQUEST, "OAUTH_USER_HAS_NO_PASSWORD");
  }
}
