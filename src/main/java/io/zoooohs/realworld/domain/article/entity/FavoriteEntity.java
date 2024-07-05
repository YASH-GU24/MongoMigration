package io.zoooohs.realworld.domain.article.entity;

import io.zoooohs.realworld.domain.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;



public class FavoriteEntity extends BaseEntity {
    private Long userId;

    // Private constructor to enforce the use of builder
    private FavoriteEntity(Long userId) {
        this.userId = userId;
    }

    // Getter for userId
    public Long getUserId() {
        return userId;
    }

    // Setter for userId
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Static method to access the builder
    public static Builder builder() {
        return new Builder();
    }

    // Builder Inner Class
    public static class Builder {
        private Long userId;

        // Setter-like method for userId in the builder
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        // Method to build the FavoriteEntity
        public FavoriteEntity build() {
            return new FavoriteEntity(userId);
        }
    }
}
