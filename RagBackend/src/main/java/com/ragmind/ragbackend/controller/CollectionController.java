package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.entity.Collection;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rag/collection")
class CollectionController {

    @GetMapping("/")
    List<Collation> getAllUserCollections() {
        return null;
    }

    @GetMapping("/{collectionId}")
    Collation getUserCollections(@PathVariable String collectionId) {
        return null;
    }

    @PostMapping("/")
    void addCollection(Collection collection) {

    }

    @DeleteMapping("/{collectionId}")
    void deleteCollection(@PathVariable String collectionId){

    }


}
