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


@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthenticationController {
    
    private final AuthenticationService authService;

    @PostMapping("login")
    public ResponseEntity<Response<RefreshTokenResponse>> login(
        @RequestBody LoginDTO credentials
    ){
        Response<RefreshTokenResponse> resp = null;

        try{
            RefreshTokenResponse tokens = authService.authenticate(credentials.getLogin(), credentials.getPassword());
            resp = new OkResponse<>(tokens);
        } catch (Exception e) {
            resp = new UnauthorizedResponse<>(null, "Unauthorized");
        }
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @PostMapping("refresh")
    public ResponseEntity<Response<RefreshTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        Response<RefreshTokenResponse> resp = null;

        if(Boolean.FALSE.equals(authService.isRefreshToken(refreshToken)))
        {
            resp = new UnauthorizedResponse<>(null, "Token is not a refresh token");
        }
        else {

            if (Boolean.TRUE.equals(authService.validateToken(refreshToken))) {
                String username = authService.extractUsername(refreshToken);
                
                String newAccessToken = authService.generateAccessToken(username);
                String newRefreshToken = authService.generateRefreshToken(username);

                resp = new OkResponse<>(new RefreshTokenResponse(newAccessToken, newRefreshToken));
            }
            else
                resp = new UnauthorizedResponse<>(null, "Token is not a refresh token");
        }

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }
}
