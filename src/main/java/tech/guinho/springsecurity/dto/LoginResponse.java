package tech.guinho.springsecurity.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
