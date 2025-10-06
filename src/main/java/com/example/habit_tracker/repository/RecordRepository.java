package com.example.habit_tracker.repository;

import com.example.habit_tracker.entity.Record;
import com.example.habit_tracker.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByHabit(Habit habit);
    Record findByHabitAndDate(Habit habit, LocalDate date);
}