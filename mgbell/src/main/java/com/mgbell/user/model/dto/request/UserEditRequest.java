package com.mgbell.user.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {
    private String nickName;
    private String name;
    private String phoneNumber;
}
