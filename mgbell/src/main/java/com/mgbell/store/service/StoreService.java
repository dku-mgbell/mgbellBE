package com.mgbell.store.service;

import com.mgbell.favorite.model.entity.Favorite;
import com.mgbell.favorite.repository.FavoriteRepository;
import com.mgbell.store.model.entity.Image;
import com.mgbell.global.s3.service.S3Service;
import com.mgbell.store.exception.AlreadyHasStoreException;
import com.mgbell.store.exception.StoreNotFoundException;
import com.mgbell.store.model.dto.response.StoreForUserResponse;
import com.mgbell.store.repository.ImageRepository;
import com.mgbell.user.exception.UserHasNoAuthorityException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.store.model.dto.request.StoreEditRequest;
import com.mgbell.store.model.dto.request.StoreRegisterRequest;
import com.mgbell.store.model.dto.response.StoreResponse;
import com.mgbell.store.model.entity.Status;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    @Transactional
    public void register(StoreRegisterRequest request, List<MultipartFile> requestImages, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if(user.getUserRole().isUser()) throw new UserHasNoAuthorityException();
        if(user.getStore() != null) throw new AlreadyHasStoreException();

        Store store = new Store(
                request.getStoreName(),
                request.getOwnerName(),
                request.getContact(),
                request.getBusinessRegiNum(),
                request.getAddress(),
                request.getLongitude(),
                request.getLatitude(),
                request.getStoreType(),
                Status.INACTIVE,
                user);

        if(requestImages != null)
            saveImages(store, requestImages);

        storeRepository.save(store);
    }

    @Transactional
    public void approve(Long storeId, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getUserRole().isAdmin()) throw new UserHasNoAuthorityException();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        store.setStatus(Status.ACTIVE);
    }

    @Transactional
    public void edit(StoreEditRequest request, List<MultipartFile> images, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getUserRole().isOwner()) throw new UserHasNoAuthorityException();

        Store store = storeRepository.findByUserId(id)
                .orElseThrow(StoreNotFoundException::new);

//        LocalDateTime updateAt = store.getUpdatedAt();
//        if(updateAt != null && updateAt.isAfter(LocalDateTime.now().minusMinutes(5))) {
//            log.info(LocalDateTime.now().toString());
//            throw new NotEnoughTimeHasPassedException();
//        }

        store.updateStore(
                request.getStoreName(),
                request.getOwnerName(),
                request.getContact(),
                request.getAddress(),
                request.getLongitude(),
                request.getLatitude(),
                request.getStoreType()
        );

        store.setStatus(Status.INACTIVE);

        updateImages(store, images);
    }

    @Transactional
    public void delete(Long id) {
        Store store = storeRepository.findByUserId(id)
                        .orElseThrow(StoreNotFoundException::new);
        store.setPost(null);
        List<Favorite> favorite = favoriteRepository.findByStoreId(store.getId());

        deleteFavorite(favorite);

        storeRepository.deleteById(id);
    }

    private void deleteFavorite(List<Favorite> favorite) {
        for (Favorite favoriteItem : favorite) favoriteRepository.delete(favoriteItem);
    }

    @Transactional
    public void saveImages(Store store, List<MultipartFile> requestImages) {

        requestImages.forEach(currImage -> {
            String fileDir = store.getStoreName() + "/" + currImage.getOriginalFilename();

            s3Service.upload(currImage, fileDir);

            Image image = Image.builder()
                    .contentType(currImage.getContentType())
                    .fileName(currImage.getOriginalFilename())
                    .originalFileDir(fileDir)
                    .thumbnailFileDir("thumbnail/" + fileDir)
                    .store(store)
                    .build();

            imageRepository.save(image);
            store.getImages().add(image);
        });
    }

    @Transactional
    public void updateImages(Store store, List<MultipartFile> requestImages) {
        List<Image> images = store.getImages();
        for(Image image : images) {
            imageRepository.delete(image);
            s3Service.delete(image.getOriginalFileDir());
            s3Service.delete(image.getThumbnailFileDir());
        }

        saveImages(store, requestImages);
    }

    public StoreForUserResponse getStore(Long storeId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        List<String> images = store.getImages().stream().map(Image::getOriginalFileDir).toList();

        return StoreForUserResponse.builder()
                .storeName(store.getStoreName())
                .businessRegiNum(store.getBusinessRegiNum())
                .address(store.getAddress())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .startAt(store.getPost().getStartAt())
                .endAt(store.getPost().getEndAt())
                .originalFileDir(images)
                .build();
    }

    public Page<StoreResponse> getAllStores(Pageable pageable) {
        Page<Store> stores = storeRepository.findAll(pageable);

        return getStoreResponse(stores);
    }

    public Page<StoreResponse> getApprovedStore(Pageable pageable) {
        Page<Store> stores = storeRepository.findByStatus(Status.ACTIVE, pageable);
        return getStoreResponse(stores);
    }

    public Page<StoreResponse> getNotApprovedStore(Pageable pageable) {
        Page<Store> stores = storeRepository.findByStatus(Status.INACTIVE, pageable);

        return getStoreResponse(stores);
    }

    public StoreResponse getMyStoreInfo(Long id) {
        Store store = storeRepository.findByUserId(id)
                .orElseThrow(StoreNotFoundException::new);
        List<String> images = store.getImages().stream().map(Image::getOriginalFileDir).toList();

        return StoreResponse.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .businessRegiNum(store.getBusinessRegiNum())
                .address(store.getAddress())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .storeType(store.getStoreType())
                .status(store.getStatus())
                .originalFileDir(images)
                .build();
    }

    public Page<StoreResponse> getStoreResponse(Page<Store> stores) {

        return stores.map(store -> {
            List<String> images = store.getImages().stream().map(Image::getOriginalFileDir).toList();

            return new StoreResponse(
                    store.getId(),
                    store.getStoreName(),
                    store.getBusinessRegiNum(),
                    store.getAddress(),
                    store.getLongitude(),
                    store.getLatitude(),
                    store.getStoreType(),
                    store.getStatus(),
                    images
            );
        });
    }
}
