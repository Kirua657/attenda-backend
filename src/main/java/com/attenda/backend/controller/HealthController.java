package com.attenda.backend.controller;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 開発用のAPIとデータベースの接続確認。 */
@RestController
public class HealthController {
    private final JdbcTemplate jdbc;

    public HealthController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Integer result = jdbc.queryForObject("SELECT 1", Integer.class);
        return Map.of("status", "ok", "db", result);
    }
}
