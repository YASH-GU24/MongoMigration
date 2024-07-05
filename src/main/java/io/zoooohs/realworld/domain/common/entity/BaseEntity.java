package io.zoooohs.realworld.domain.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
public class BaseEntity {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }




    public static class BaseEntityBuilder {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long id;

        BaseEntityBuilder() {}

        public BaseEntityBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BaseEntityBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BaseEntityBuilder id(Long id) {
            this.id = id;
            return this;
        }


    }
}
