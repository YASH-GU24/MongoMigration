package io.zoooohs.realworld.domain.profile.repository;

import com.mongodb.BasicDBObject;
import io.zoooohs.realworld.domain.profile.entity.FollowEntity;
import io.zoooohs.realworld.domain.user.entity.UserEntity;
import io.zoooohs.realworld.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FollowRepositoryImpl implements FollowRepository {
    private final UserRepository mongoUserRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<FollowEntity> findByFolloweeIdAndFollowerId(Long followeeId, Long followerId) {
        Optional<UserEntity> follower = mongoUserRepository.findById(followerId);
        if (follower.isEmpty()) {
            return Optional.empty();
        }
        return follower.get().getFollowsById(followeeId);
    }

    @Override
    public List<FollowEntity> findByFollowerId(Long id) {
        Optional<UserEntity> user = mongoUserRepository.findById(id);
        if (user.isEmpty()) {
            return new ArrayList<>();
        }
        return user.get().getFollows();
    }

    @Override
    public FollowEntity addFollow(UserEntity follower, Long followeeId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(follower.getId()));
        Update update = new Update();
        FollowEntity follow = FollowEntity.builder().follower(follower.getId()).followee(followeeId).build();
        update.push("follows", follow);
        mongoTemplate.updateFirst(query, update, UserEntity.class);
        return follow;
    }

    @Override
    public void delete(FollowEntity follow) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(follow.getFollower()));
        Update update = new Update();
        update.pull("follows", new BasicDBObject("followee", follow.getFollowee()));
        mongoTemplate.findAndModify(query, update, UserEntity.class);
    }
}
