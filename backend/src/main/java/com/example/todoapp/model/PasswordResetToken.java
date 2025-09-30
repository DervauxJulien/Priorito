package com.example.todoapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime expiryDate;
    @OneToOne
    private User user;

    public PasswordResetToken(User user, String token){
        this.user = user;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusMinutes(15);
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
