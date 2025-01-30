package br.lightbase.helios.users.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.lightbase.helios.users.entity.User;
import br.lightbase.helios.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository repo;

    public User getUserByLogin(String login) {
        return repo.findByLogin(login).orElse(null);
    }

    public List<User> getUsersByName(String nameFilter){
        return repo.findByFullnameContaining(nameFilter).orElse(List.of());
    }

    public User save(User newUser) {
        String pwd = newUser.getPassword();
        pwd = new BCryptPasswordEncoder().encode(pwd);
        newUser.setPassword(pwd);

        return repo.save(newUser);
    }

    public Page<User> findUsers(String login, String name, Boolean active, Pageable pageable) {
        return repo.findUsers(login, name, active, pageable);
    }
}
