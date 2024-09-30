package com.mgbell.store.service;

import com.mgbell.store.exception.AlreadyHasStoreException;
import com.mgbell.store.exception.StoreNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void register(StoreRegisterRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if(user.getUserRole().isUser()) throw new UserHasNoAuthorityException();
        if(user.getStore() != null) throw new AlreadyHasStoreException();

        Store store = new Store(
                request.getName(),
                request.getAddress(),
                request.getStoreType(),
//                request.getImage(),
                Status.INACTIVE,
                user);

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
    public void edit(StoreEditRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getUserRole().isOwner()) throw new UserHasNoAuthorityException();

        Store store = storeRepository.findByUserId(id)
                .orElseThrow(StoreNotFoundException::new);

        store.setStatus(Status.INACTIVE);
    }

    @Transactional
    public void delete(Long id) {
        Store store = storeRepository.findByUserId(id)
                        .orElseThrow(StoreNotFoundException::new);
        store.setPost(null);
        storeRepository.deleteById(id);
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

    public StoreResponse getStoreInfo(Long id) {
        Store store = storeRepository.findByUserId(id)
                .orElseThrow(StoreNotFoundException::new);

        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .storeType(store.getStoreType())
                .status(store.getStatus())
                .build();
    }

    public Page<StoreResponse> getStoreResponse(Page<Store> stores) {
        return stores.map(store ->
                new StoreResponse(
                        store.getId(),
                        store.getName(),
                        store.getAddress(),
                        store.getStoreType(),
                        store.getStatus()
                ));
    }


//    private void saveFiles(MultipartFile files, Store store) {
//        List<RequestFile> requestFiles = fileUploadService.uploadFiles(
//                FileUploadRequest.ofList(files));
//        List<PostFile> postFiles = new ArrayList<>();
//
//        for (RequestFile file : requestFiles) {
//            PostFile.PostFileBuilder builder = PostFile.builder()
//                    .fileName(file.getOriginalName())
//                    .mediaType(file.getMediaType().toString())
//                    .fileId(file.getFileId());
//
//            postFiles.add(builder.build());
//        }
//
//        for (PostFile file : postFiles) {
//            file.setPost(post);
//        }
//    }
}
