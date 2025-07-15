package tech.guinho.springsecurity.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tech.guinho.springsecurity.dto.FeedDto;
import tech.guinho.springsecurity.dto.FeedItemDto;
import tech.guinho.springsecurity.dto.TweetDto;
import tech.guinho.springsecurity.model.Role;
import tech.guinho.springsecurity.model.Tweet;
import tech.guinho.springsecurity.model.User;
import tech.guinho.springsecurity.repository.TweetRepository;
import tech.guinho.springsecurity.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@RestController
public class TweetController {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/tweet")
    public ResponseEntity<Void> createTweet(@RequestBody TweetDto tweetDto, JwtAuthenticationToken token) {
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Tweet tweet = new Tweet();

        tweet.setContent(tweetDto.content());
        tweet.setUser(user.get());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id, JwtAuthenticationToken token){
        Optional<Tweet> tweet = tweetRepository.findById(id);
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));

        if(tweet.isEmpty() || user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

       Boolean isAdmin = user.get().getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || tweet.get().getUser().getId().equals(UUID.fromString(token.getName()))){
            tweetRepository.delete(tweet.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<FeedItemDto> tweets = tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"))
                .map(tweet ->
                        new FeedItemDto(
                                tweet.getId(),
                                tweet.getContent(),
                                tweet.getUser().getUsername()));
        return ResponseEntity.ok(new FeedDto(tweets.getContent(), page, pageSize, tweets.getTotalPages(), tweets.getTotalElements()));

    }
}
