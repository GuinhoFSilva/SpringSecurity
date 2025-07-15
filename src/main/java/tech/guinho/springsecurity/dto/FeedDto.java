package tech.guinho.springsecurity.dto;

import java.util.List;

public record FeedDto(List<FeedItemDto> feedItens, Integer page, Integer pageSize, Integer totalPages, Long totalElements) {
}
