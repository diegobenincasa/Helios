package br.lightbase.helios.users.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.lightbase.helios.users.entity.User;

public interface UserRepositoryCustom {
    
    public Page<User> findUsers(String login, String name, Boolean active, Pageable pageable);
}
