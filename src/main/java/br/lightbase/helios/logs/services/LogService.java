package br.lightbase.helios.logs.services;

import org.springframework.stereotype.Service;

import br.lightbase.helios.logs.entities.RequestLog;
import br.lightbase.helios.logs.repositories.LogRepository;

@Service
public class LogService {
    
    private final LogRepository logRepository;

    LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public RequestLog save(RequestLog log) {
        return logRepository.save(log);
    }

    public RequestLog findByUsername(String username) {
        return logRepository.findByUsername(username);
    }
}
