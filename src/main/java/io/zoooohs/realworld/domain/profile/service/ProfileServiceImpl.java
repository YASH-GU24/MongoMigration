package io.zoooohs.realworld.domain.profile.service;

import io.zoooohs.realworld.domain.profile.dto.ProfileDto;
import io.zoooohs.realworld.domain.profile.entity.FollowEntity;
import io.zoooohs.realworld.domain.profile.repository.FollowRepository;
import io.zoooohs.realworld.domain.user.entity.UserEntity;
import io.zoooohs.realworld.domain.user.repository.UserRepository;
import io.zoooohs.realworld.exception.AppException;
import io.zoooohs.realworld.exception.Error;
import io.zoooohs.realworld.security.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository mongoUserRepository;
    private final FollowRepository mongoFollowRepository;

    @Override
    public ProfileDto getProfile(String name, AuthUserDetails authUserDetails) {
        UserEntity user = mongoUserRepository.findByUsername(name).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        Boolean following = mongoFollowRepository.findByFolloweeIdAndFollowerId(user.getId(), authUserDetails.getId()).isPresent();
        return convertToProfile(user, following);
    }

    @Transactional
    @Override
    public ProfileDto followUser(String name, AuthUserDetails authUserDetails) {
        UserEntity followee = mongoUserRepository.findByUsername(name).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        UserEntity follower = mongoUserRepository.findById(authUserDetails.getId()).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        mongoFollowRepository.findByFolloweeIdAndFollowerId(followee.getId(), follower.getId())
                .ifPresent(follow -> {throw new AppException(Error.ALREADY_FOLLOWED_USER);});
        mongoFollowRepository.addFollow(follower, followee.getId());
        return convertToProfile(followee, true);
    }

    @Transactional
    @Override
    public ProfileDto unfollowUser(String name, AuthUserDetails authUserDetails) {
        UserEntity followee = mongoUserRepository.findByUsername(name).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        Long followerId = authUserDetails.getId();

        FollowEntity follow = mongoFollowRepository.findByFolloweeIdAndFollowerId(followee.getId(), followerId)
                .orElseThrow(() -> new AppException(Error.FOLLOW_NOT_FOUND));
        mongoFollowRepository.delete(follow);

        return convertToProfile(followee, false);
    }

    @Override
    public ProfileDto getProfileByUserId(Long userId, AuthUserDetails authUserDetails) {
        UserEntity user = mongoUserRepository.findById(userId).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        Boolean following = mongoFollowRepository.findByFolloweeIdAndFollowerId(user.getId(), authUserDetails.getId()).isPresent();

        return convertToProfile(user, following);
    }

    private ProfileDto convertToProfile(UserEntity user, Boolean following) {
        return ProfileDto.builder()
                .username(user.getUsername())
                .bio(user.getBio())
                .image(user.getImage())
                .following(following)
                .build();
    }

}
