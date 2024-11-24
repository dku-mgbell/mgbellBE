package com.mgbell.notification.service;

import com.google.firebase.messaging.*;
import com.mgbell.notification.exception.FcmTokenNotRegisteredException;
import com.mgbell.notification.model.dto.request.NotificationRequest;
import com.mgbell.notification.model.dto.request.TokenRegisterRequest;
import com.mgbell.notification.model.entity.NotificationHistory;
import com.mgbell.notification.repository.FcmRedisRepository;
import com.mgbell.notification.repository.NotificationRepository;
import com.mgbell.order.exception.OrderNotFoundException;
import com.mgbell.order.model.entity.Order;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final FcmRedisRepository fcmRedisRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final StoreRepository surveyRepository;
    private final Map<Long, String> tokenMap = new HashMap<>();
    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);
    private final TaskScheduler taskScheduler;
    private final OrderRepository orderRepository;

    @Transactional
    public void sendNotification(NotificationRequest notificationRequest) {
        if (!hasKey(notificationRequest.getEmail())) {
            log.info("토큰이 등록되어 있지 않음");
            throw new FcmTokenNotRegisteredException();
        }
        String token = getToken(notificationRequest.getEmail());
        String title = notificationRequest.getTitle();
        String body = notificationRequest.getBody();

        Message message = Message.builder()
                .setToken(token)
                .putData("title", title)
                .putData("body", body)
                // 안드로이드
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build())
                // 아이폰
                .setApnsConfig(ApnsConfig.builder()
                        .putHeader("apns-priority", "10")
                        .setAps(Aps.builder()
                                .setAlert(ApsAlert.builder()
                                        .setTitle(title)
                                        .setBody(body)
                                        .build())
                                .setBadge(42)
                                .build())
                        .build())
                .build();

        send(message);

        NotificationHistory notification = NotificationHistory.builder()
                .title(title)
                .content(body)
                .build();
        notificationRepository.save(notification);
    }

    public void sendSubmitNotification(String studentId) {
        NotificationRequest notificationRequest = new NotificationRequest(
                studentId,
                "설문 제출 완료",
                "설문이 성공적으로 제출되었습니다!"
        );

        sendNotification(notificationRequest);
    }

    public void sendFriendNotification(String studentId) {
        NotificationRequest notificationRequest = new NotificationRequest(
                studentId,
                "친구 요청 도착",
                "친구 요청이 도착했습니다!"
        );

        sendNotification(notificationRequest);
    }

    @Transactional
    public void sendOrderReminder(String email, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        LocalDateTime sendAt = order.getPickupTime().atDate(LocalDate.now()).minusMinutes(10);

        NotificationRequest request = new NotificationRequest(
                email,
                "마감백 픽업 시간 알림",
                "픽업 예정 시간 10분 전입니다."
        );

        taskScheduler.schedule(
                () -> sendNotification(request), sendAt.toInstant(KST_OFFSET)
        );
    }


    public void send(Message message) {
        FirebaseMessaging.getInstance().sendAsync(message);
        log.info("Send NotificationHistory Success");
    }

    private String getToken(String studentId) {
        return fcmRedisRepository.getToken(studentId);
    }

    private boolean hasKey(String studentId) {
        return fcmRedisRepository.hasKey(studentId);
    }

    public void register(TokenRegisterRequest request){
        fcmRedisRepository.saveToken(request);
    }

    public void deleteToken(String studentId){
        fcmRedisRepository.deleteToken(studentId);
        log.info("Delete Token Success");
    }
}
