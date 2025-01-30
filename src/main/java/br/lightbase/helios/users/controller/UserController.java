package br.lightbase.helios.users.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.lightbase.helios.common.responses.BadRequestResponse;
import br.lightbase.helios.common.responses.NotFoundResponse;
import br.lightbase.helios.common.responses.OkResponse;
import br.lightbase.helios.common.responses.Response;
import br.lightbase.helios.users.entity.User;
import br.lightbase.helios.users.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("user")
public class UserController {
    
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<Response<Page<User>>> getUsers(
        @RequestParam(required = false) String login,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean active,
        Pageable pageable
    ) {

        try {
            Page<User> resp = userService.findUsers(login, name, active, pageable);
            if(resp.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse<Page<User>>(null, "No users found"));
            
            return ResponseEntity.ok().body(new OkResponse<Page<User>>(resp));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse<Page<User>>(null, e.getMessage()));
        }
    }

    @PostMapping("")
    public ResponseEntity<Response<User>> saveUser(
        @RequestBody User newUser) {
        
        if(newUser.getLogin() == null || newUser.getPassword() == null || newUser.getFullname() == null ||
           newUser.getLogin().isEmpty() || newUser.getPassword().isEmpty() || newUser.getFullname().isEmpty()
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse<User>(null, "One or more required parameters missing"));
        }

        try {
            newUser = userService.save(newUser);
            return ResponseEntity.ok(new OkResponse<User>(newUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BadRequestResponse<User>(null, e.getMessage()));
        }
    }
}
