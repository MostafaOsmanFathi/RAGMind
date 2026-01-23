package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.rabbitmq.AskRabbitmqRequestDto;
import com.ragmind.ragbackend.dto.rabbitmq.DocumentRabbitmqRequestDto;
import com.ragmind.ragbackend.rabbitmq.DocumentRabbitmqProducer;
import com.ragmind.ragbackend.rabbitmq.RagRabbitmqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitmqService {
    final DocumentRabbitmqProducer documentRabbitmqProducer;
    final RagRabbitmqProducer ragRabbitmqProducer;

    public RabbitmqService(DocumentRabbitmqProducer documentRabbitmqProducer, RagRabbitmqProducer ragRabbitmqProducer) {
        this.documentRabbitmqProducer = documentRabbitmqProducer;
        this.ragRabbitmqProducer = ragRabbitmqProducer;
    }


    public void sendDocumentTask(DocumentRabbitmqRequestDto documentRabbitmqRequestDto) {
        documentRabbitmqProducer.sendDocumentTask(documentRabbitmqRequestDto);
    }

    public void sendAskTask(AskRabbitmqRequestDto askRabbitmqRequestDto) {
        ragRabbitmqProducer.sendAskTask(askRabbitmqRequestDto);
    }

}
