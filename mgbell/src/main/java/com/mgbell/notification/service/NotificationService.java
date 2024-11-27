package com.mgbell.notification.service;

import com.google.firebase.messaging.*;
import com.mgbell.notification.exception.FcmTokenNotRegisteredException;
import com.mgbell.notification.model.dto.request.MultiNotificationRequest;
import com.mgbell.notification.model.dto.request.NotificationRequest;
import com.mgbell.notification.model.dto.request.OfficialNotificationRequest;
import com.mgbell.notification.model.dto.request.TokenRegisterRequest;
import com.mgbell.notification.model.entity.NotificationHistory;
import com.mgbell.notification.repository.FcmRedisRepository;
import com.mgbell.notification.repository.NotificationRepository;
import com.mgbell.order.exception.OrderNotFoundException;
import com.mgbell.order.model.entity.Order;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.model.entity.user.UserRole;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Transactional
    public void sendNotificationToMultiple(MultiNotificationRequest request, List<String> registrationTokens) {
        String title = request.getTitle();
        String body = request.getBody();

        MulticastMessage multicastMessage =  MulticastMessage.builder()
                // 알림 보낼 유저 목록
                .addAllTokens(registrationTokens)
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


        sendMultiMessage(multicastMessage);

        NotificationHistory notification = NotificationHistory.builder()
                .title(title)
                .content(body)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendOfficialNotification(OfficialNotificationRequest request) {
        List<String> userEmails = new ArrayList<>();

//        if (request.getTo() == UserRole.OWNER) {
//            List<User> owners = userRepository.findByUserRole(request.getTo());
//
//            owners.forEach(currOwner -> {
//                userEmails.add(currOwner.getEmail());
//            });
//        } else if (request.getTo() == UserRole.USER){
//            List<User> users = userRepository.findByUserRole(request.getTo());
//
//            users.forEach(currOwner -> {
//                userEmails.add(currOwner.getEmail());
//            });
//        }

        List<User> users = userRepository.findByUserRole(request.getTo());

        users.forEach(currUser -> {
            userEmails.add(currUser.getEmail());
        });

        List<String> userTokens = new ArrayList<>();
        userEmails.forEach(currEmail -> {
                    userTokens.add(getToken(currEmail));
                }
        );

        MultiNotificationRequest notiRequest = new MultiNotificationRequest(
                "마감벨 공지 📢",
                request.getBody()
        );

        sendNotificationToMultiple(notiRequest, userTokens);
    }

    @Transactional
    public void sendOpenNotification(List<String> userEmails, String storeName) {
        List<String> userTokens = new ArrayList<>();
        userEmails.forEach(currEmail ->{
                        userTokens.add(getToken(currEmail));
                }
        );

        MultiNotificationRequest request = new MultiNotificationRequest(
                "마감벨 알림 🔔",
                storeName + "에서 판매를 시작했어요!"
        );

        sendNotificationToMultiple(request, userTokens);
    }

    @Transactional
    public void sendChangeNotification(List<String> userEmails, String storeName) {
        List<String> userTokens = new ArrayList<>();
        userEmails.forEach(currEmail ->{
                    userTokens.add(getToken(currEmail));
                }
        );

        MultiNotificationRequest request = new MultiNotificationRequest(
                "마감벨 알림 🔔",
                storeName + "의 수량이 변경됐어요!"
        );

        sendNotificationToMultiple(request, userTokens);
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

    @Transactional
    public void sendOrderRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        NotificationRequest request = new NotificationRequest(
                user.getEmail(),
                "마감벨 주문 알림",
                "주문이 들어왔어요!"
        );

        sendNotification(request);
    }

    @Transactional
    public void sendOrderCancel(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        NotificationRequest request = new NotificationRequest(
                user.getEmail(),
                "마감벨 주문 취소 알림",
                "주문이 취소됐어요"
        );

        sendNotification(request);
    }

    public void send(Message message) {
        FirebaseMessaging.getInstance().sendAsync(message);
        log.info("Send Notification Success");
    }

    public void sendMultiMessage(MulticastMessage multicastMessage) {
        FirebaseMessaging.getInstance().sendEachForMulticastAsync(multicastMessage);
        log.info("Send Multi Notification Success");
    }

    private String getToken(String email) {
        return fcmRedisRepository.getToken(email);
    }

    private boolean hasKey(String email) {
        return fcmRedisRepository.hasKey(email);
    }

    public void register(Long userId, TokenRegisterRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        fcmRedisRepository.saveToken(user.getEmail(), request.getToken());
    }

    public void deleteToken(String email){
        fcmRedisRepository.deleteToken(email);
        log.info("Delete Token Success");
    }
}
