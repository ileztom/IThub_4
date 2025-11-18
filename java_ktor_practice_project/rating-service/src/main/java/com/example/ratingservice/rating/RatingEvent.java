package com.example.ratingservice.rating;

public class RatingEvent {
    public Long userId;
    public Long courseId;
    public int score;

    public RatingEvent() {}

    public RatingEvent(Long userId, Long courseId, int score) {
        this.userId = userId;
        this.courseId = courseId;
        this.score = score;
    }
}
