package io.zoooohs.realworld.domain.user.entity;

import io.zoooohs.realworld.domain.common.entity.AutoIncrementId;
import io.zoooohs.realworld.domain.common.entity.BaseEntity;
import io.zoooohs.realworld.domain.profile.entity.FollowEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Document("users")
public class UserEntity extends BaseEntity implements AutoIncrementId {
    @Id
    public ObjectId _id;
    private Long id;
    private String username;
    private String email;
    private String password;
    private String bio;
    private String image;

    private List<FollowEntity> follows = new ArrayList<>();


    public Optional<FollowEntity> getFollowsById(Long id) {
        if (this.follows == null) {
            return Optional.empty(); // Return an empty Optional if follows is null
        }

        List<FollowEntity> follower = this.follows.stream()
                .filter(follow -> follow.getFollowee().equals(id))
                .collect(Collectors.toList());
        if (follower.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(follower.get(0));
    }

    // Getters and Setters
    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<FollowEntity> getFollows() {
        return follows;
    }

    public void setFollows(List<FollowEntity> follows) {
        this.follows = follows;
    }
}
