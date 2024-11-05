package com.mgbell.favorite.service;

import com.mgbell.favorite.exception.FavoriteNotExist;
import com.mgbell.favorite.model.dto.request.FavoriteRequest;
import com.mgbell.favorite.model.dto.response.FavoriteResponse;
import com.mgbell.favorite.model.entity.Favorite;
import com.mgbell.favorite.repository.FavoriteRepository;
import com.mgbell.post.model.entity.Post;
import com.mgbell.store.exception.StoreNotFoundException;
import com.mgbell.store.model.entity.Store;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void favoriteUpdate(FavoriteRequest request, Long userId) {
        boolean hasFavoriteRecord = favoriteRepository.existsByStoreIdAndUserId(request.getStoreId(), userId);
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(StoreNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if(!hasFavoriteRecord && request.isStatus()) {
            Favorite favorite = Favorite.builder()
                    .store(store)
                    .user(user)
                    .build();

            store.increaseFavorited();
            favoriteRepository.save(favorite);
        } else if(hasFavoriteRecord && !request.isStatus()) {
            Favorite favorite = favoriteRepository.findByStoreIdAndUserId(store.getId(), userId)
                    .orElseThrow(FavoriteNotExist::new);

            store.decreaseFavorited();
            favoriteRepository.delete(favorite);
        }
    }

    public Page<FavoriteResponse> getFavoriteList(Pageable pageable, Long userId) {
        Page<Favorite> favorites = favoriteRepository.findByUserId(pageable, userId);

        return getFavoriteResponses(favorites);
    }

    private Page<FavoriteResponse> getFavoriteResponses(Page<Favorite> favorites) {
        return favorites.map(currFavorite -> {
            Store store = currFavorite.getStore();
            Post post = store.getPost();

            return new FavoriteResponse(
                    store.getPost().getPostId(),
                    store.getStoreName(),
                    post.getBagName(),
                    post.isOnSale(),
                    post.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                    post.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                    store.getAddress(),
                    store.getLongitude(),
                    store.getLatitude(),
                    post.getCostPrice(),
                    post.getSalePrice(),
                    post.getAmount());
        });
    }
}
