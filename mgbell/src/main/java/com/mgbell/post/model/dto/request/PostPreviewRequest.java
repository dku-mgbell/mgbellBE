package com.mgbell.post.model.dto.request;

import com.mgbell.store.model.entity.StoreType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class PostPreviewRequest {
    private StoreType storeType;
    private Boolean onSale;
}
