package com.tonmoyd0116.ratelimiter.controller;

import com.tonmoyd0116.ratelimiter.dto.ApiResponse;
import com.tonmoyd0116.ratelimiter.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class RateLimiterController {

    @GetMapping("/api/health")
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

        return ResponseEntity.ok(response);
    }

}
