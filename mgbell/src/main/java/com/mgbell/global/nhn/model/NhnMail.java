package com.mgbell.global.nhn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@Builder
@AllArgsConstructor
public class NhnMail {
    private String senderAddress;
    private String senderName;
    private String title;
    private String body;
    private ArrayList<Receiver> receiverList;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Receiver {

        private String receiveMailAddr;
        private ReceiveType receiveType;
    }
}
