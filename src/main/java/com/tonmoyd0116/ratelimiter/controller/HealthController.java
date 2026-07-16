package com.tonmoyd0116.ratelimiter.controller;

import com.tonmoyd0116.ratelimiter.dto.ApiResponse;
import com.tonmoyd0116.ratelimiter.dto.HealthResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<HealthResponse>> healthCheck(){
        HealthResponse healthResponse = new HealthResponse("UP");
        ApiResponse<HealthResponse> response =
                new ApiResponse<>(
                        true,
                        200,
                        "Api is Running Successfully",
                        Instant.now(),
                        healthResponse
                );
        log.info("Health Check API hit was made at time {}",Instant.now());
        return ResponseEntity.ok(response);
    }
}
