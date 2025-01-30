package br.lightbase.helios.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.lightbase.helios.users.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom{
    
    Optional<User> findByLogin(String login);
    Optional<List<User>> findByFullnameContaining(String txt);
}
