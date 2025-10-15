package com.backend.petplace.domain.review.entity;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewId")
	private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "placeId", nullable = false)
    private Place place;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String imageUrl;

    @Builder
    public Review(Long id, User user, Place place, String content, int rating, String imageUrl) {
        this.id = id;
        this.user = user;
        this.place = place;
        this.content = content;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }
}
