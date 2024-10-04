package com.mgbell.post.exception;

public class PostNotFoundException extends RuntimeException {
  public PostNotFoundException() {
    super("POST NOT FOUND");
  }
}
