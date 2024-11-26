package com.mgbell.notification.model.dto.request;

import com.mgbell.user.model.entity.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OfficialNotificationRequest {
    private UserRole to;
    private String body;
}
