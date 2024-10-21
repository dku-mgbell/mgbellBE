package com.mgbell.favorite.exception;

public class FavoriteNotExist extends RuntimeException {
    public FavoriteNotExist() {
        super("FAVORITE NOT EXIST");
    }
}
