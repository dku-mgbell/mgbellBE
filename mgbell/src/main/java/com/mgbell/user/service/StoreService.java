package com.mgbell.user.service;

import com.mgbell.user.exception.StoreNotFoundException;
import com.mgbell.user.exception.UserHasNoAuthorityException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.dto.request.StoreRegisterRequest;
import com.mgbell.user.model.dto.response.StoreResponse;
import com.mgbell.user.model.entity.store.Status;
import com.mgbell.user.model.entity.store.Store;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.repository.StoreRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Page<StoreResponse> getStoreResponse(Page<Store> stores) {
        return stores.map(store ->
                new StoreResponse(
                        store.getId(),
                        store.getName(),
                        store.getAddress(),
                        store.getStoreType()
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
