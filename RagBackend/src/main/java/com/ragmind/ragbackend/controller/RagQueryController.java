package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.dto.rabbitmq.AskRabbitmqRequestDto;
import com.ragmind.ragbackend.dto.rabbitmq.DocumentRabbitmqRequestDto;
import com.ragmind.ragbackend.entity.CollectionChat;
import com.ragmind.ragbackend.service.RabbitmqService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/rag/collections/{collectionId}/queries")
class RagQueryController {

    final RabbitmqService rabbitmqService;

    public RagQueryController(RabbitmqService rabbitmqService) {
        this.rabbitmqService = rabbitmqService;
    }

    @PostMapping("/ask")
    ResponseEntity<?> createQuery(@PathVariable String collectionId, @RequestBody AskRabbitmqRequestDto askRabbitmqRequestDto, Authentication authentication) {
        askRabbitmqRequestDto.setCollectionName(collectionId);
        try {
            rabbitmqService.sendAskTask(askRabbitmqRequestDto, authentication);
            return ResponseEntity.ok("Ask task sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send ask task: " + e.getMessage());
        }
    }


    @GetMapping("/chat-history")
    ResponseEntity<?> getChat(@PathVariable String collectionId, @RequestBody DocumentRabbitmqRequestDto documentRabbitmqRequestDto) {
        documentRabbitmqRequestDto.setCollectionName(collectionId);
        try {
            rabbitmqService.sendDocumentTask(documentRabbitmqRequestDto);
            return ResponseEntity.ok("Document task sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send document task: " + e.getMessage());
        }
    }

}
