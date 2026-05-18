package com.tripperr.api.auth.repository;

import com.tripperr.api.auth.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByUserId(String userId);
}
