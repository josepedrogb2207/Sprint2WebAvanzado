package com.lta.backend.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import com.lta.backend.services.TopicMessageService;

@Component
public class StrConsumerListener {

    private static final Logger log = LoggerFactory.getLogger(StrConsumerListener.class);
    
    @Autowired
    private TopicMessageService topicMessageService;

    @KafkaListener(
            groupId = "group-new-topics",
            topicPartitions = @TopicPartition(topic = "paciente-topic", partitions = {"0"}),
            containerFactory = "validMessageContainerFactory")
    public void pacientePartition0(String message){
        log.info("Recibiendo mensaje de paciente-topic partición 0: {}", message);
        topicMessageService.handlePacientePartition0(message);
    }

    @KafkaListener(
            groupId = "group-new-topics",
            topicPartitions = @TopicPartition(topic = "paciente-topic", partitions = {"1"}),
            containerFactory = "validMessageContainerFactory")
    public void pacientePartition1(String message){
        log.info("Recibiendo mensaje de paciente-topic partición 1: {}", message);
        topicMessageService.handlePacientePartition1(message);
    }

    @KafkaListener(
            groupId = "group-new-topics",
            topicPartitions = @TopicPartition(topic = "paciente-topic", partitions = {"2"}),
            containerFactory = "validMessageContainerFactory")
    public void pacientePartition2(String message){
        log.info("Recibiendo mensaje de paciente-topic partición 2: {}", message);
        topicMessageService.handlePacientePartition2(message);
    }

    @KafkaListener(
            groupId = "group-new-topics",
            topicPartitions = @TopicPartition(topic = "citas-topic", partitions = {"0"}),
            containerFactory = "validMessageContainerFactory")
    public void citasPartition0(String message){
        log.info("Recibiendo mensaje de citas-topic partición 0: {}", message);
        topicMessageService.handleCitasPartition0(message);
    }

    @KafkaListener(
            groupId = "group-new-topics",
            topicPartitions = @TopicPartition(topic = "citas-topic", partitions = {"1"}),
            containerFactory = "validMessageContainerFactory")
    public void citasPartition1(String message){
        log.info("Recibiendo mensaje de citas-topic partición 1: {}", message);
        topicMessageService.handleCitasPartition1(message);
    }

    @KafkaListener(
            groupId = "group-new-topics",
            topicPartitions = @TopicPartition(topic = "citas-topic", partitions = {"2"}),
            containerFactory = "validMessageContainerFactory")
    public void citasPartition2(String message){
        log.info("Recibiendo mensaje de citas-topic partición 2: {}", message);
        topicMessageService.handleCitasPartition2(message);
    }

    @KafkaListener(
            groupId = "group-new-topics",
            topicPartitions = @TopicPartition(topic = "estado-topic", partitions = {"0"}),
            containerFactory = "validMessageContainerFactory")
    public void estadoPartition0(String message){
        log.info("Recibiendo mensaje de estado-topic partición 0: {}", message);
        topicMessageService.handleEstadoPartition0(message);
    }

    @KafkaListener(
            groupId = "group-new-topics",
            topicPartitions = @TopicPartition(topic = "estado-topic", partitions = {"1"}),
            containerFactory = "validMessageContainerFactory")
    public void estadoPartition1(String message){
        log.info("Recibiendo mensaje de estado-topic partición 1: {}", message);
        topicMessageService.handleEstadoPartition1(message);
    }

}






