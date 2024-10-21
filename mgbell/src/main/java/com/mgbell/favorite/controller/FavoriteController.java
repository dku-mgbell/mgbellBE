package com.mgbell.favorite.controller;

import com.mgbell.favorite.model.dto.request.FavoriteRequest;
import com.mgbell.favorite.model.entity.Favorite;
import com.mgbell.favorite.service.FavoriteService;
import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.UserAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @UserAuth
    @PostMapping
    public void favoriteUpdate(@RequestBody FavoriteRequest request, JwtAuthentication auth) {
        favoriteService.favoriteUpdate(request, auth.getUserId());
    }
}
