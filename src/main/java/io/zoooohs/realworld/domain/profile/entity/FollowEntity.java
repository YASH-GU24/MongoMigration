package io.zoooohs.realworld.domain.profile.entity;

import io.zoooohs.realworld.domain.common.entity.BaseEntity;

public class FollowEntity extends BaseEntity {
    private Long followee;
    private Long follower;

    // All-args constructor
    public FollowEntity(Long followee, Long follower) {
        this.followee = followee;
        this.follower = follower;
    }

    // Getter for followee
    public Long getFollowee() {
        return followee;
    }

    // Getter for follower
    public Long getFollower() {
        return follower;
    }

    // Builder static inner class
    public static class Builder {
        private Long followee;
        private Long follower;

        public Builder followee(Long followee) {
            this.followee = followee;
            return this;
        }

        public Builder follower(Long follower) {
            this.follower = follower;
            return this;
        }

        public FollowEntity build() {
            return new FollowEntity(followee, follower);
        }
    }

    // Static method to access the builder
    public static Builder builder() {
        return new Builder();
    }
}
