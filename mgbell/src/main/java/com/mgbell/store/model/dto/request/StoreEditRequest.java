package com.mgbell.store.model.dto.request;

import com.mgbell.store.model.entity.StoreType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreEditRequest {
    private String name;
    private String address;
    private String longitude;
    private String latitude;
    private StoreType storeType;
//    private MultipartFile image;
}
