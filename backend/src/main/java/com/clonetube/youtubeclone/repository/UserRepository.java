package com.clonetube.youtubeclone.repository;

import com.clonetube.youtubeclone.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {
}
