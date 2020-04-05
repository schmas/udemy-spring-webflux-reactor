package com.br.schmidt.udemy.reactivespring.repository;

import com.br.schmidt.udemy.reactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
}
