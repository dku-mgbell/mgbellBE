package com.mgbell.post.model.dto.response;

import com.mgbell.post.model.entity.Cost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class PostPreviewResponse {
    private Long id;
    private String title;
    private String storeName;
    private Cost cost;
    private int amount;
}
