package com.example.nmm;

import com.example.nmm.models.Scores;

import java.util.List;

public class User {
    public String email;
    public List<Scores> scoresList;

    public User() {

    }

    public User(String email, List<Scores> scoresList) {
        this.email = email;
        this.scoresList = scoresList;
    }
}
