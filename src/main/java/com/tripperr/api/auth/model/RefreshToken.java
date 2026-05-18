package com.tripperr.api.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "refresh_tokens")
public class RefreshToken {

    @Id
    private String id;

    @Indexed(unique = true)
    private String tokenHash;

    @Indexed
    private String userId;

    private Instant expiresAt;

    private boolean revoked;

    @CreatedDate
    private Instant createdAt;
}
