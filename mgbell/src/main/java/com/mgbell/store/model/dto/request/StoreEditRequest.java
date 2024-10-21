package com.mgbell.store.model.dto.request;

import com.mgbell.store.model.entity.StoreType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreEditRequest {
    @NotNull
    private String storeName;
    @NotNull
    private String ownerName;
    @NotNull
    private String contact;
    @NotNull
    private String address;
    private String longitude;
    private String latitude;
    @NotNull
    private StoreType storeType;
//    private MultipartFile image;
}
