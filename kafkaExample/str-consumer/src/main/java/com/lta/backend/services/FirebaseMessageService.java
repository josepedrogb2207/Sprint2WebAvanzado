package com.lta.backend.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;

@Service
public class FirebaseMessageService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseMessageService.class);
    private final Firestore firestore;

    public FirebaseMessageService(Firestore firestore) {
        this.firestore = firestore;
    }

    public void saveTopicMessage(String topic, int partition, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("topic", topic);
        payload.put("partition", partition);
        payload.put("message", message);
        payload.put("createdAt", FieldValue.serverTimestamp());

        firestore.collection("kafka_messages").add(payload);
        log.info("Mensaje enviado a Firebase en coleccion kafka_messages: topic={}, partition={}", topic, partition);
    }
}
