package br.lightbase.helios.logs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.lightbase.helios.logs.entities.RequestLog;

@Repository
public interface LogRepository extends JpaRepository<RequestLog, Long> {
    
    RequestLog findByUsername(String username);
}
