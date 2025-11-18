package com.example.ratingservice.rating;

import jakarta.persistence.*;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long courseId;
    private int score;

    public Rating() {}

    public Rating(Long userId, Long courseId, int score) {
        this.userId = userId;
        this.courseId = courseId;
        this.score = score;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long u) { this.userId = u; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long c) { this.courseId = c; }
    public int getScore() { return score; }
    public void setScore(int s) { this.score = s; }
}
