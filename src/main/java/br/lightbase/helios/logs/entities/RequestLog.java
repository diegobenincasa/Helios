package br.lightbase.helios.logs.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "request_logs", schema = "siafibus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String uri;
    @Column(nullable = true)
    private String body;
    private String method;
    @Column(nullable = true)
    private Boolean passed;
    private Integer status;
    @Column(insertable = false, updatable = false)
    private LocalDateTime datetime;
}
