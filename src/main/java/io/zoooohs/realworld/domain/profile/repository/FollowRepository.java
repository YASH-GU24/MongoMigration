package io.zoooohs.realworld.domain.profile.repository;

import io.zoooohs.realworld.domain.profile.entity.FollowEntity;
import io.zoooohs.realworld.domain.user.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface FollowRepository {
    Optional<FollowEntity> findByFolloweeIdAndFollowerId(Long followeeId, Long followerId);

    List<FollowEntity> findByFollowerId(Long id);

    FollowEntity addFollow(UserEntity follower, Long followeeId);

    void delete(FollowEntity follow);
}
