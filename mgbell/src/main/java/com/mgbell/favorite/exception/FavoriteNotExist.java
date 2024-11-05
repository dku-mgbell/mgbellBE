package com.mgbell.favorite.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class FavoriteNotExist extends CustomException {
    public FavoriteNotExist() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "FAVORITE_NOT_EXIST");
    }
}
