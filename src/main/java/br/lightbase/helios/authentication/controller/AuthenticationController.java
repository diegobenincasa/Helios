package br.lightbase.helios.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.lightbase.helios.authentication.controller.dto.LoginDTO;
import br.lightbase.helios.authentication.controller.dto.RefreshTokenRequest;
import br.lightbase.helios.authentication.controller.dto.RefreshTokenResponse;
import br.lightbase.helios.authentication.service.AuthenticationService;
import br.lightbase.helios.common.responses.OkResponse;
import br.lightbase.helios.common.responses.Response;
import br.lightbase.helios.common.responses.UnauthorizedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthenticationController {
    
    private final AuthenticationService authService;

    @PostMapping("login")
    public Mono<ResponseEntity<Response<RefreshTokenResponse>>> login(
        @RequestBody Mono<LoginDTO> credentialsMono
    ){
        return credentialsMono.flatMap(credentials -> {
            try {
                RefreshTokenResponse tokens = authService.authenticate(credentials.getLogin(), credentials.getPassword());
                Response<RefreshTokenResponse> resp = new OkResponse<>(tokens);
                return Mono.just(ResponseEntity.status(resp.getStatus()).body(resp));
            } catch (Exception e) {
                Response<RefreshTokenResponse> resp = new UnauthorizedResponse<>(null, "Unauthorized");
                return Mono.just(ResponseEntity.status(resp.getStatus()).body(resp));
            }
        });
    }

    @PostMapping("refresh")
    public Mono<ResponseEntity<Response<RefreshTokenResponse>>> refreshToken(@RequestBody Mono<RefreshTokenRequest> requestMono) {
        return requestMono.flatMap(request -> {
            String refreshToken = request.getRefreshToken();
            Response<RefreshTokenResponse> resp;

            if (Boolean.FALSE.equals(authService.isRefreshToken(refreshToken))) {
                resp = new UnauthorizedResponse<>(null, "Token is not a refresh token");
                return Mono.just(ResponseEntity.status(resp.getStatus()).body(resp));
            }

            if (Boolean.TRUE.equals(authService.validateToken(refreshToken))) {
                String username = authService.extractUsername(refreshToken);
                String newAccessToken = authService.generateAccessToken(username);
                String newRefreshToken = authService.generateRefreshToken(username);
                resp = new OkResponse<>(new RefreshTokenResponse(newAccessToken, newRefreshToken));
            } else {
                resp = new UnauthorizedResponse<>(null, "Invalid refresh token");
            }

            return Mono.just(ResponseEntity.status(resp.getStatus()).body(resp));
        });
    }
}
