package io.zoooohs.realworld.domain.user.service;

import io.zoooohs.realworld.domain.user.dto.UserDto;
import io.zoooohs.realworld.exception.AppException;
import io.zoooohs.realworld.exception.Error;
import io.zoooohs.realworld.security.AuthUserDetails;
import io.zoooohs.realworld.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class UserServiceImplTest {
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtUtils jwtUtils;
    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, jwtUtils, passwordEncoder);
    }

    @Test
    void whenValidRegistrationInfo_thenSaveNewUserAndReturnNewUserDto() {
        UserDto.Registration registration = UserDto.Registration.builder().email("test@test.com").username("testman").password("password").build();

        when(jwtUtils.encode(anyString())).thenReturn("token.test.needed");
        when(passwordEncoder.encode(anyString())).thenReturn("b{testpasswordencodedstring}");

        UserDto actual = userService.registration(registration);

        verify(userRepository, times(1)).save(any(UserEntity.class));

        assertEquals(registration.getEmail(), actual.getEmail());
        assertEquals(registration.getUsername(), actual.getUsername());
        assertEquals("", actual.getBio());
        assertNull(actual.getImage());
        assertNotNull(actual.getToken());
    }

    @Test
    void whenDuplicatedUserRegistration_thenThrowDuplicationException() {
        UserDto.Registration registration = UserDto.Registration.builder().email("test@test.com").username("testman").password("password").build();

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(List.of(UserEntity.builder().build()));

        try {
            userService.registration(registration);
            fail();
        } catch (AppException e) {
            assertEquals(Error.DUPLICATED_USER, e.getError());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void whenValidLoginInfo_thenReturnUserDto() {
        UserDto.Login login = UserDto.Login.builder().email("test@test.com").password("password123").build();

        UserEntity userEntity = UserEntity.builder()
                .username("username")
                .email(login.getEmail())
                .password("test-password-encoded")
                .build();


        when(jwtUtils.encode(anyString())).thenReturn("token.test.needed");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(eq(login.getPassword()), eq(userEntity.getPassword()))).thenReturn(true);

        UserDto actual = userService.login(login);

        assertNotNull(actual.getToken());
        assertEquals(login.getEmail(), actual.getEmail());
    }

    @Test
    void whenInvalidLoginInfo_thenThrow422() {
        UserDto.Login login = UserDto.Login.builder().email("test@test.com").password("password123").build();

        try {
            userService.login(login);
            fail();
        } catch (AppException e) {
            assertEquals(Error.LOGIN_INFO_INVALID, e.getError());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void whenAuthUser_thenReturnUser() {
        AuthUserDetails authUserDetails = AuthUserDetails.builder()
                .id(1L)
                .email("email@meail.com")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .username("name")
                .email(authUserDetails.getEmail())
                .password("test-password-encoded")
                .build();

        when(userRepository.findById(eq(authUserDetails.getId()))).thenReturn(Optional.of(userEntity));

        UserDto actual = userService.currentUser(authUserDetails);

        assertEquals(authUserDetails.getEmail(), actual.getEmail());
    }

    @Test
    void whenAuthUserNotFound_throw404() {
        AuthUserDetails authUserDetails = AuthUserDetails.builder()
                .id(1L)
                .build();
        try {
            userService.currentUser(authUserDetails);
            fail();
        } catch (AppException e) {
            assertEquals(Error.USER_NOT_FOUND, e.getError());
            assertEquals(HttpStatus.NOT_FOUND, e.getError().getStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void whenUpdateUserDto_thenReturnUpdatedUserDto() {
        AuthUserDetails authUserDetails = AuthUserDetails.builder()
                .id(1L)
                .build();
        UserDto.Update update = UserDto.Update.builder().username("newName").bio("newBio").build();

        UserEntity userEntity = UserEntity.builder()
                .username("username")
                .email("email@email.com")
                .password("test-password-encoded")
                .build();
        when(userRepository.findById(eq(authUserDetails.getId()))).thenReturn(Optional.of(userEntity));

        UserDto actual = userService.update(update, authUserDetails);

        assertEquals(update.getUsername(), actual.getUsername());
        assertEquals(update.getBio(), actual.getBio());
        assertNotNull(actual.getUsername());
        assertNotNull(actual.getEmail());
    }

    @ParameterizedTest
    @MethodSource("invalidUpdate")
    void whenInvalidUpdateDto_thenThrow422(UserDto.Update update) {
        AuthUserDetails authUserDetails = AuthUserDetails.builder()
                .id(1L)
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .username("username")
                .email("email@email.com")
                .password("test-password-encoded")
                .build();


        when(userRepository.findById(eq(authUserDetails.getId()))).thenReturn(Optional.of(userEntity));
        if (update.getEmail() != null) {
            when(userRepository.findByEmail(eq("dup@email.com"))).thenReturn(Optional.of(UserEntity.builder().id(2L).email("dup@email.com").build()));
        } else if (update.getUsername() != null) {
            when(userRepository.findByUsername(eq("dupName"))).thenReturn(Optional.of(UserEntity.builder().id(2L).username("dupName").build()));
        }

        try {
            userService.update(update, authUserDetails);
            fail();
        } catch (AppException e) {
            assertEquals(Error.DUPLICATED_USER, e.getError());
        } catch (Exception e) {
            fail();
        }
    }

    public static Stream<Arguments> invalidUpdate() {
        return Stream.of(
                Arguments.of(UserDto.Update.builder().username("dupName").build()),
                Arguments.of(UserDto.Update.builder().email("dup@email.com").build())
        );
    }
}
