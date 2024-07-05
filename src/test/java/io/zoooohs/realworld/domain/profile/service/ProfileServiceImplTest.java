package io.zoooohs.realworld.domain.profile.service;

import io.zoooohs.realworld.domain.profile.dto.ProfileDto;
import io.zoooohs.realworld.exception.AppException;
import io.zoooohs.realworld.exception.Error;
import io.zoooohs.realworld.security.AuthUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceImplTest {
    ProfileService profileService;

    AuthUserDetails authUserDetails;

    @Mock
    UserRepository userRepository;

    @Mock
    FollowRepository followRepository;

    private UserEntity expectedUser;

    @BeforeEach
    void setUp() {
        profileService = new ProfileServiceImpl(userRepository, followRepository);
        authUserDetails = AuthUserDetails.builder()
                .id(1L)
                .email("email@email.com")
                .build();

        expectedUser = UserEntity.builder()
                .id(2L)
                .username("expectedUser")
                .email("expected@email.com")
                .bio("expected bio")
                .image("expected_image_path")
                .build();

        when(userRepository.findByUsername(eq(expectedUser.getUsername()))).thenReturn(Optional.of(expectedUser));
    }

    @Test
    void whenValidUsername_thenReturnProfile() {
        ProfileDto actual = profileService.getProfile(expectedUser.getUsername(), authUserDetails);

        assertEquals(expectedUser.getUsername(), actual.getUsername());
        assertEquals(expectedUser.getBio(), actual.getBio());
        assertEquals(expectedUser.getImage(), actual.getImage());
    }

    @Test
    void whenFollowValidUsername_thenFollowAndReturnProfile() {
        ProfileDto actual = profileService.followUser(expectedUser.getUsername(), authUserDetails);

        assertTrue(actual.getFollowing());
    }

    @Test
    void whenFollowFollowedUsername_thenThrow422() {
        when(followRepository.findByFolloweeIdAndFollowerId(expectedUser.getId(), authUserDetails.getId())).thenReturn(Optional.of(FollowEntity.builder().build()));
        try {
            profileService.followUser(expectedUser.getUsername(), authUserDetails);
            fail();
        } catch (AppException e) {
            assertEquals(Error.ALREADY_FOLLOWED_USER, e.getError());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    void whenUnfollowFollowedUsername_thenReturnProfile() {
        when(followRepository.findByFolloweeIdAndFollowerId(expectedUser.getId(), authUserDetails.getId())).thenReturn(Optional.of(FollowEntity.builder().build()));

        ProfileDto actual = profileService.unfollowUser(expectedUser.getUsername(), authUserDetails);

        assertFalse(actual.getFollowing());
    }

    @Test
    void whenUnfollowNotFollowedUsername_thenThrow404() {
        try {
            profileService.unfollowUser(expectedUser.getUsername(), authUserDetails);
            fail();
        } catch (AppException e) {
            assertEquals(Error.FOLLOW_NOT_FOUND, e.getError());
        } catch (Exception e) {
            fail();
        }
    }
}
