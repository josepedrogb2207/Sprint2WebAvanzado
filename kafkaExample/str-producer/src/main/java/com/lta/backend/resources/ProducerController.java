package com.lta.backend.resources;

import com.lta.backend.services.StringProducerService;
import com.lta.backend.services.ResponseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private StringProducerService producer;

    @GetMapping("/menu")
    public String sendMenu(@RequestParam String message) {
        return producer.sendMessage(message);
    }

    @GetMapping("/test")
    public String sendTest(@RequestParam String message) {
        return producer.sendMessage(message);
    }

    @GetMapping("/response")
    public Map<String, Object> getLatestResponse(@RequestParam(defaultValue = "false") boolean clear) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("response", ResponseListener.getLatestResponse());
        payload.put("updatedAt", ResponseListener.getUpdatedAt());

        if (clear) {
            ResponseListener.clear();
        }

        return payload;
    }
}