package com.example.ratingservice.rating;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingRepository repo;
    private final KafkaTemplate<String, RatingEvent> kafkaTemplate;

    public RatingController(RatingRepository repo, KafkaTemplate<String, RatingEvent> kafkaTemplate) {
        this.repo = repo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public Rating create(@RequestBody RatingRequest request) {
        Rating rating = new Rating(request.userId(), request.courseId(), request.score());
        Rating saved = repo.save(rating);

        // Отправляем событие в Kafka
        RatingEvent event = new RatingEvent(
                saved.getUserId(),
                saved.getCourseId(),
                saved.getScore()
        );
        kafkaTemplate.send("ratings", event);

        return saved;
    }
}

record RatingRequest(Long userId, Long courseId, int score) {}
