package com.grupo1.mindbody.chatbot.model;

import com.grupo1.mindbody.iam.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String preferredSports;

    private String preferredTimes;

    @Enumerated(EnumType.STRING)
    private FitnessLevel fitnessLevel;

    private String goals;

    @Column(columnDefinition = "text")
    private String healthNotes;

    private LocalDateTime completedAt;

    private LocalDateTime updatedAt;
}
