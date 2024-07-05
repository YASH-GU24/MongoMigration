package io.zoooohs.realworld.domain.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
public class ProfileDto {
    private String username;
    private String bio;
    private String image;
    private Boolean following;

    // Constructor made private to enforce the use of the builder
    private ProfileDto(String username, String bio, String image, Boolean following) {
        this.username = username;
        this.bio = bio;
        this.image = image;
        this.following = following;
    }

    // Getters for each field
    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }

    public Boolean isFollowing() {
        return following;
    }

    // Static builder class
    public static class Builder {
        private String username;
        private String bio;
        private String image;
        private Boolean following;

        public Builder() {}

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder following(Boolean following) {
            this.following = following;
            return this;
        }

        public ProfileDto build() {
            return new ProfileDto(username, bio, image, following);
        }
    }

    // Static method to access the builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Single {
        private ProfileDto profile;

        // No-args constructor
        public Single() {}

        // All-args constructor
        public Single(ProfileDto profile) {
            this.profile = profile;
        }

        // Getter for profile
        public ProfileDto getProfile() {
            return profile;
        }

        // Setter for profile (if modification is needed after object creation)
        public void setProfile(ProfileDto profile) {
            this.profile = profile;
        }

        // Static builder class for the nested Single class
        public static class Builder {
            private ProfileDto profile;

            public Builder() {}

            public Builder profile(ProfileDto profile) {
                this.profile = profile;
                return this;
            }

            public Single build() {
                return new Single(profile);
            }
        }

        // Static method to access the builder for Single
        public static Builder builder() {
            return new Builder();
        }
    }
}
