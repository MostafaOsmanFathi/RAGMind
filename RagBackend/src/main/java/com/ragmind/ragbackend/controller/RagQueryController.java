package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.entity.CollectionChat;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rag/collections/{collectionId}/queries")
class RagQueryController {


    @PostMapping("/ask")
    void createQuery(@PathVariable String collectionId){

    }


    @GetMapping("/chat-history")
    List<CollectionChat> getChat(@PathVariable String collectionId){
        return null;
    }

}
