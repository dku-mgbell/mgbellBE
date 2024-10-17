package com.mgbell.global.nhn.service;

import com.mgbell.global.nhn.model.NhnMail;
import com.mgbell.global.nhn.model.ReceiveType;
import com.mgbell.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class NhnService {

    @Value("${nhn.email}")
    private String NHN_SENDER_EMAIL;
    @Value("${nhn.url}")
    private String NHN_URL;
    @Value("${nhn.secretKey}")
    private String NHN_SECRET_KEY;

    private final UserRepository userRepository;
    private final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public void sendEmail(String email, String title, String content) {
        NhnMail nhnMail = makeEmail(email, title, content);

        log.info("Sending mail to: " + email);
        log.info("title: " + title);
        log.info("content: " + content);

        ResponseEntity<String> response = webClient.post()
                .uri(NHN_URL)
                .header("X-Secret-Key", NHN_SECRET_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(nhnMail)
                .retrieve() // client message 전송
                .toEntity(String.class)
                .block();

        if (response == null || response.getStatusCode().isError()) {
            throw new RuntimeException("FAILED TO SEND EMAIL");
        }
    }

    private NhnMail makeEmail(String email, String title, String content) {
        ArrayList<NhnMail.Receiver> receiveList = new ArrayList<>();

        receiveList.add(
                NhnMail.Receiver.builder()
                        .receiveMailAddr(email)
                        .receiveType(ReceiveType.MRT0)
                        .build()
        );

        return NhnMail.builder()
                .senderAddress(NHN_SENDER_EMAIL)
                .senderName("마감벨")
                .receiverList(receiveList)
                .title(title)
                .body(content)
                .build();
    }
}
