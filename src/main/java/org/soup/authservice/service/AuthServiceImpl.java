package org.soup.authservice.service;


import org.apache.commons.lang3.RandomStringUtils;
import org.soup.authservice.model.User;
import org.soup.authservice.repository.UserRepository;
import org.soup.authservice.repository.entity.UserEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        UserEntity user = new UserEntity();

        String randomSalt = RandomStringUtils.randomAlphanumeric(20);
        user.setUsername(username);
        user.setSalt(randomSalt);
        user.setPassword(passwordEncoder.encode(randomSalt + password));
        userRepository.save(user);
    }

    @Override
    public User authenticateUser(String username, String password) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        String salt = userEntity.getSalt();
        if (!passwordEncoder.matches(salt + password, userEntity.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new User(userEntity.getId(), userEntity.getUsername());
    }
}
