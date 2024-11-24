package com.mgbell;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class MgbellApplication {

    public static void main(String[] args) {
        SpringApplication.run(MgbellApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // timezone 설정
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")));
    }

}
