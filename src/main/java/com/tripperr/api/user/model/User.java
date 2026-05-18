package com.tripperr.api.user.model;

import com.tripperr.api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User extends BaseDocument {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    private String passwordHash;

    @Builder.Default
    private Set<Role> roles = new HashSet<>(Set.of(Role.USER));

    @Builder.Default
    private boolean enabled = true;

    private String profileImageUrl;
}
