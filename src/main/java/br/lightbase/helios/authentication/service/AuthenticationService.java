package br.lightbase.helios.authentication.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.lightbase.helios.authentication.controller.dto.RefreshTokenResponse;
import br.lightbase.helios.config.jwt.JwtUtil;
import br.lightbase.helios.users.entity.User;
import br.lightbase.helios.users.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public RefreshTokenResponse authenticate(String username, String password) {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new SecurityException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new SecurityException("Invalid credentials");
        }
        if (Boolean.FALSE.equals(user.getActive()))
            throw new SecurityException("User inactive");

        RefreshTokenResponse resp = new RefreshTokenResponse();
        resp.setAccessToken(jwtUtil.generateToken(username).replace("Bearer ", ""));
        resp.setRefreshToken(jwtUtil.generateRefreshToken(username).replace("Bearer ", ""));

        return resp;
    }

    public Boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String extractUsername(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    public String generateAccessToken(String username) {
        return jwtUtil.generateToken(username);
    }

    public String generateRefreshToken(String username) {
        return jwtUtil.generateRefreshToken(username);
    }

    public Boolean isRefreshToken(String token) {
        return jwtUtil.isRefreshToken(token);
    }
}
