package com.mgbell.user.model.dto.response;

import com.mgbell.user.model.entity.store.StoreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private String name;
    private String address;
    private StoreType storeType;
}
