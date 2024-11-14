package com.mgbell.user.service;

import com.mgbell.global.auth.jwt.JwtProvider;
import com.mgbell.global.auth.jwt.JwtToken;
import com.mgbell.order.model.entity.Order;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.review.model.entity.Review;
import com.mgbell.review.repository.ReviewRepository;
import com.mgbell.user.exception.IncorrectPassword;
import com.mgbell.user.exception.UserAlreadyExistException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.dto.request.*;
import com.mgbell.user.model.dto.response.*;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.repository.TokenRedisRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;

    @Transactional
    public void signUp(String signupToken, SignupRequest request) {
        if(isDuplicateEmail(request.getEmail())) {
            throw new UserAlreadyExistException();
        }
        if(!tokenRedisRepository.getToken(request.getEmail()).equals(signupToken)) {
            throw new IncorrectPassword();
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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

        List<Order> orders = user.getOrder();
        List<CurrentOrderResponse> currentOrder = orders.stream()
                .filter(currOrder -> currOrder.getState().equals(OrderState.REQUESTED)
                        || currOrder.getState().equals(OrderState.ACCEPTED))
                .map(
                        currOrder -> new CurrentOrderResponse(
                                currOrder.getId(),
                                currOrder.getStore().getStoreName(),
                                currOrder.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                currOrder.getState()
                        )
                ).toList();

        return new MyPageResponse(
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
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
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

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        Store store = user.getStore();
        List<Review> review = user.getReview();
        List<Order> order = user.getOrder();

        if (store != null) {
            store.getPost().setStore(null);
            store.getPost().setUser(null);
            postRepository.delete(store.getPost());

            store.setPost(null);

            reviewRepository.deleteAll(store.getReviews());

            storeRepository.delete(store);
            user.setStore(null);
        }

        if (review != null) {
            review.forEach(currReview -> {
                        currReview.setUser(null);
                        currReview.setOrder(null);
                    }
            );

            user.setReview(null);
        }

        if (order != null) {
            order.forEach(currOrder -> {
                        currOrder.setUser(null);
                        currOrder.setStore(null);
                        orderRepository.deleteById(currOrder.getId());
                    }
            );

            user.setOrder(null);
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
    public void resetPwd(PasswordResetRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

//        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
//            throw new IncorrectPassword();
//        }

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
}
