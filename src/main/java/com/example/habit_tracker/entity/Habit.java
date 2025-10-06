package com.example.habit_tracker.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Record> records = new ArrayList<>();

    public List<Record> getRecords() { return records; }

    public Habit() {}
    public Habit(String name, User user) {
        this.name = name;
        this.user = user;
    }

      @Column(nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    //Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}