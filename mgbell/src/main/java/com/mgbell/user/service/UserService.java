package com.mgbell.user.service;

import com.mgbell.favorite.model.entity.Favorite;
import com.mgbell.favorite.repository.FavoriteRepository;
import com.mgbell.global.auth.jwt.JwtProvider;
import com.mgbell.global.auth.jwt.JwtToken;
import com.mgbell.global.s3.service.S3Service;
import com.mgbell.order.model.entity.Order;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.review.model.entity.Review;
import com.mgbell.review.model.entity.ReviewImage;
import com.mgbell.review.repository.ReviewImageRepositoty;
import com.mgbell.review.repository.ReviewRepository;
import com.mgbell.store.model.entity.StoreImage;
import com.mgbell.store.repository.StoreImageRepository;
import com.mgbell.store.service.StoreService;
import com.mgbell.user.exception.*;
import com.mgbell.user.model.dto.request.*;
import com.mgbell.user.model.dto.response.*;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.repository.TokenRedisRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;
    private final StoreImageRepository storeImageRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewImageRepositoty reviewImageRepositoty;
    private final StoreService storeService;

    @Value("${s3.url}")
    private String s3url;

    @Transactional
    public void signUp(String signupToken, SignupRequest request) {
        if(isDuplicateEmail(request.getEmail())) {
            throw new UserAlreadyExistException();
        }
        if(!tokenRedisRepository.getToken(request.getEmail()).equals(signupToken)) {
            throw new IncorrectToken();
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .userRole(request.getUserRole())
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            JwtToken token = jwtProvider.issue(user);
            return new LoginResponse(token.getAccessToken(), token.getRefreshToken(), user.getUserRole());
        } else {
            throw new IncorrectPassword();
        }
    }

    public MyPageResponse myPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Order> orders = orderRepository.findByUserId(userId);
        List<CurrentOrderResponse> currentOrder = orders.stream()
                .filter(currOrder -> currOrder.getState().equals(OrderState.REQUESTED)
                        || currOrder.getState().equals(OrderState.ACCEPTED))
                .map(currOrder ->
                        new CurrentOrderResponse(
                                currOrder.getId(),
                                currOrder.getStore().getStoreName(),
                                currOrder.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                currOrder.getState(),
                                s3url +
                                        URLEncoder.encode(
                                                storeImageRepository.findByStoreId(currOrder.getStore().getId())
                                                        .get(0)
                                                        .getOriginalFileDir(), StandardCharsets.UTF_8
                                        )
                        )
                ).toList();

        return new MyPageResponse(
                user.getNickname(),
                user.getName(),
                user.getOrderCnt(),
                user.getCarbonReduction(),
                user.getTotalDiscount(),
                currentOrder
        );
    }

    public ReissueResponse reissue(ReissueRequest request) {
        JwtToken token = jwtProvider.reissue(request.getRefreshToken());

        return new ReissueResponse(
                token.getAccessToken(),
                token.getRefreshToken()
        );
    }

    public boolean isDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void oAuthSignup(OAuthSignupRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.setUserRole(request.getUserRole());
        user.editUserInfo(
                request.getNickname(),
                request.getName(),
                request.getPhoneNumber()
        );
    }

    public String oAuthLogin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return user.getEmail();
    }

    @Transactional
    public void edit(UserEditRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.editUserInfo(
                request.getNickname(),
                request.getName(),
                request.getPhoneNumber()
        );
    }

    @Transactional
    public void setNickName(NickNameRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.setNickname(request.getNickName());
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        Store store = storeRepository.findByUserId(id)
                .orElse(null);

        if (store != null) {
            storeService.delete(id);
        } else {
            List<Review> review = reviewRepository.findByUserId(id);
            List<Order> order = orderRepository.findByUserId(id);
            List<Favorite> favorite = favoriteRepository.findByUserId(id);

            if (review != null) {
                review.forEach(currReview -> {
                            deleteReviewImages(currReview);

                            currReview.setUser(null);
                            currReview.setOrder(null);
                        }
                );
            }

            if (order != null) {
                order.forEach(currOrder -> {
                            currOrder.setStore(null);
                            orderRepository.delete(currOrder);
                        }
                );
            }

            if (favorite != null) {
                favorite.forEach(currFavorite -> {
                    favoriteRepository.delete(currFavorite);
                });
            }
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public void updatePwd(PasswordUpdateRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IncorrectPassword();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void resetPwd(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if(!tokenRedisRepository.getToken(user.getEmail()).equals(request.getToken())) {
            throw new IncorrectToken();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }
    public UserInfoResponse whoAmI(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return new UserInfoResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Transactional
    public void deleteReviewImages(Review review) {
        List<ReviewImage> images = review.getImages();
        for(ReviewImage image : images) {
            reviewImageRepositoty.delete(image);
            s3Service.delete(image.getOriginalFileDir());
            s3Service.delete(image.getThumbnailFileDir());
        }
    }

    @Transactional
    public void deleteStoreImages(Store store) {
        List<StoreImage> images = storeImageRepository.findByStoreId(store.getId());
        for(StoreImage image : images) {
            s3Service.delete(image.getOriginalFileDir());
            s3Service.delete(image.getThumbnailFileDir());
            storeImageRepository.delete(image);
        }
    }
}
