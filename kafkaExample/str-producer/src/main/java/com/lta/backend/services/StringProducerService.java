package com.lta.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StringProducerService {

    private static final Logger log = LoggerFactory.getLogger(StringProducerService.class);

    private static final Pattern ROUTE_PATTERN = Pattern.compile("(?i)^(.*?)(paciente|pacientes|cita|citas|estado)(\\d+)$");

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public String sendMessage(String message){
        KafkaRoute route = resolveRoute(message);

        kafkaTemplate.send(route.topic, route.partition, null, message).whenComplete((result,ex) -> {
            if(ex != null){
                log.error("Error, al enviar el mensaje: {}",ex.getMessage());
                return;
            }
            log.info("Mensaje enviado con exito: {}",result.getProducerRecord().value());
            log.info("Particion {}, Offset {}", result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
        });

        return String.format("Mensaje enviado a %s partición %d", route.topic, route.partition);
    }

    private KafkaRoute resolveRoute(String message) {
        String normalizedMessage = message == null ? "" : message.trim();
        Matcher matcher = ROUTE_PATTERN.matcher(normalizedMessage);

        if (matcher.matches()) {
            String topicSuffix = matcher.group(2).toLowerCase();
            int partition = Integer.parseInt(matcher.group(3));

            if (topicSuffix.startsWith("paciente")) {
                return new KafkaRoute("paciente-topic", partition);
            }

            if (topicSuffix.startsWith("cita")) {
                return new KafkaRoute("citas-topic", partition);
            }

            if (topicSuffix.equals("estado")) {
                return new KafkaRoute("estado-topic", partition);
            }
        }

        log.warn("Mensaje sin sufijo reconocido, se enviará al tópico por defecto: {}", normalizedMessage);
        return new KafkaRoute("str-topic", 0);
    }

    private static class KafkaRoute {
        private final String topic;
        private final int partition;

        private KafkaRoute(String topic, int partition) {
            this.topic = topic;
            this.partition = partition;
        }
    }
}














/*
String topic;
        if(message.contains("te")){
            topic = "topic-2";
        }else{
            topic = "str-topic";
        }
*/