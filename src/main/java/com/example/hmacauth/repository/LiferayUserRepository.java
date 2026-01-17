package com.example.hmacauth.repository;

import java.util.Optional;

import com.example.hmacauth.model.LiferayUser;

import org.springframework.data.repository.CrudRepository;

public interface LiferayUserRepository extends CrudRepository<LiferayUser, Long> {
    Optional<LiferayUser> findByScreenNameOrEmailAddress(String screenName, String emailAddress);
}
