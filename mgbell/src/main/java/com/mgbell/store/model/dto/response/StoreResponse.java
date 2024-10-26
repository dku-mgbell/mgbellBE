package com.mgbell.store.model.dto.response;

import com.mgbell.store.model.entity.Status;
import com.mgbell.store.model.entity.StoreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private String storeName;
    private String businessRegiNum;
    private String address;
    private String longitude;
    private String latitude;
    private StoreType storeType;
    private Status status;
    private List<String> originalFileDir;
}
