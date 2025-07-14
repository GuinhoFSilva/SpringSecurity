package tech.guinho.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.guinho.springsecurity.model.Tweet;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
}
