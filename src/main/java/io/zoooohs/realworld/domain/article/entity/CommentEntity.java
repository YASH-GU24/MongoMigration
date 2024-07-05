package io.zoooohs.realworld.domain.article.entity;

import io.zoooohs.realworld.domain.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentEntity extends BaseEntity {
    @Field("id")
    private Long id;
    private String body;
    private Long authorId;

    // Constructors (including builder) are already defined by Lombok annotations

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    // toString(), equals(), and hashCode() methods are inherited from BaseEntity
}
