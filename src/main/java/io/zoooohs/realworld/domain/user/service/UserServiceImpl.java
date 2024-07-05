package io.zoooohs.realworld.domain.user.service;

import io.zoooohs.realworld.domain.user.dto.UserDto;
import io.zoooohs.realworld.domain.user.entity.UserEntity;
import io.zoooohs.realworld.domain.user.repository.UserRepository;
import io.zoooohs.realworld.exception.AppException;
import io.zoooohs.realworld.exception.Error;
import io.zoooohs.realworld.security.AuthUserDetails;
import io.zoooohs.realworld.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository mongoUserRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registration(final UserDto.Registration registration) {
        mongoUserRepository.findByUsernameOrEmail(registration.getUsername(), registration.getEmail()).stream().findAny().ifPresent(entity -> {throw new AppException(Error.DUPLICATED_USER);});

        UserEntity userEntity = UserEntity.builder()
                .username(registration.getUsername())
                .email(registration.getEmail())
                .password(passwordEncoder.encode(registration.getPassword()))
                .bio("")
                .build();
        mongoUserRepository.save(userEntity);
        return convertEntityToDto(userEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto login(UserDto.Login login) {
        UserEntity userEntity = mongoUserRepository.findByEmail(login.getEmail()).filter(user -> passwordEncoder.matches(login.getPassword(), user.getPassword())).orElseThrow(() -> new AppException(Error.LOGIN_INFO_INVALID));
        return convertEntityToDto(userEntity);
    }

    private UserDto convertEntityToDto(UserEntity userEntity) {
        return UserDto.builder().username(userEntity.getUsername()).bio(userEntity.getBio()).email(userEntity.getEmail()).image(userEntity.getImage()).token(jwtUtils.encode(userEntity.getEmail())).build();
    }


    @Transactional(readOnly = true)
    @Override
    public UserDto currentUser(AuthUserDetails authUserDetails) {
        UserEntity userEntity = mongoUserRepository.findById(authUserDetails.getId()).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));
        return convertEntityToDto(userEntity);
    }

    @Override
    public UserDto update(UserDto.Update update, AuthUserDetails authUserDetails) {
        UserEntity userEntity = mongoUserRepository.findById(authUserDetails.getId()).orElseThrow(() -> new AppException(Error.USER_NOT_FOUND));

        if (update.getUsername() != null) {
            mongoUserRepository.findByUsername(update.getUsername())
                    .filter(found -> !found.getId().equals(userEntity.getId()))
                    .ifPresent(found -> {throw new AppException(Error.DUPLICATED_USER);});
            userEntity.setUsername(update.getUsername());
        }

        if (update.getEmail() != null) {
            mongoUserRepository.findByEmail(update.getEmail())
                    .filter(found -> !found.getId().equals(userEntity.getId()))
                    .ifPresent(found -> {throw new AppException(Error.DUPLICATED_USER);});
            userEntity.setEmail(update.getEmail());
        }

        if (update.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(update.getPassword()));
        }

        if (update.getBio() != null) {
            userEntity.setBio(update.getBio());
        }

        if (update.getImage() != null) {
            userEntity.setImage(update.getImage());
        }

        mongoUserRepository.save(userEntity);
        return convertEntityToDto(userEntity);
    }
}
