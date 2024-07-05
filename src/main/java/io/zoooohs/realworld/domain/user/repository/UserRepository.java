package io.zoooohs.realworld.domain.user.repository;

import io.zoooohs.realworld.domain.common.repository.AutoIncrementIdSave;
import io.zoooohs.realworld.domain.user.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends MongoRepository<UserEntity, String>, AutoIncrementIdSave<UserEntity> {
    Optional<UserEntity> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findById(Long id);

    List<UserEntity> findAll();


}
