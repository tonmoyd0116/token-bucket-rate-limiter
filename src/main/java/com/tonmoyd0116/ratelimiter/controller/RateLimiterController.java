package com.tonmoyd0116.ratelimiter.controller;

import com.tonmoyd0116.ratelimiter.dto.ApiResponse;
import com.tonmoyd0116.ratelimiter.dto.RatelimiterResponse;
import com.tonmoyd0116.ratelimiter.service.RateLimiterService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rate-limiter")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    @GetMapping("/allow/{clientId}")
    public ResponseEntity<ApiResponse<RatelimiterResponse>> allowRequest(@PathVariable String clientId){
        boolean allowed = rateLimiterService.allowRequest(clientId);
        HttpStatus status = allowed?HttpStatus.OK : HttpStatus.TOO_MANY_REQUESTS;

        ApiResponse<RatelimiterResponse> response = new ApiResponse<>(
          allowed,
          status.value(),
          allowed?"Request Allowed":"Too many Requests,\n Please try after some time",
          Instant.now(),
          new RatelimiterResponse(clientId,allowed)
        );

        return ResponseEntity.status(status).body(response);
    }
}
