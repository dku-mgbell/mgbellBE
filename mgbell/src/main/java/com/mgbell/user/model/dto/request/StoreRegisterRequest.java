package com.mgbell.user.model.dto.request;

import com.mgbell.user.model.entity.store.StoreType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRegisterRequest {
    private Long userId;
    private String name;
    private String address;
    private StoreType storeType;
//    private MultipartFile image;
}
