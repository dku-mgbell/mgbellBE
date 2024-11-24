package com.mgbell.global.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class FcmInitializer {
//    @Value("${fcm.certification}")
//    private String FIREBASE_CONFIG_PATH;
//
//    @PostConstruct
//    public void initFcm() {
//        try {
//            FileInputStream fileInputStream = new FileInputStream(FIREBASE_CONFIG_PATH);
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(fileInputStream))
//                    .build();
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//                log.info("FirebaseApp initialization complete");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
