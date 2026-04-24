package com.lta.backend.services;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResponseListener {

    private static volatile String response = "";
    private static volatile long updatedAt = 0L;

    @KafkaListener(topics = "str-topic-response", groupId = "group-1")
    public void listen(String message){
        response = message;
        updatedAt = System.currentTimeMillis();
    }

    public static String getLatestResponse() {
        return response;
    }

    public static long getUpdatedAt() {
        return updatedAt;
    }

    public static void clear() {
        response = "";
        updatedAt = 0L;
    }
}