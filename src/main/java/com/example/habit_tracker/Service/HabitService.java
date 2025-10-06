package com.example.habit_tracker.service;

import com.example.habit_tracker.entity.Habit;
import com.example.habit_tracker.entity.Record;
import com.example.habit_tracker.entity.User;
import com.example.habit_tracker.repository.HabitRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class HabitService {
    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public Habit addHabit(String name, User user) {
        return habitRepository.save(new Habit(name, user));
    }

    public List<Habit> getHabits(User user) {
        return habitRepository.findByUser(user);
    }

    public Optional<Habit> findById(Long id) {
        return habitRepository.findById(id);
    }

    public Map<Long, Double> calculateAchievementRates(List<Habit> habits) {
        Map<Long, Double> rates = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (Habit habit : habits) {
            long totalDays = ChronoUnit.DAYS.between(habit.getCreatedAt(), today) + 1;
            long achievedDays = habit.getRecords().stream()
                                     .filter(Record::isCompleted)
                                     .count();
            
            double rate = totalDays > 0 ? (achievedDays * 100.0 / totalDays) : 0.0;
            rates.put(habit.getId(), rate);
        }
        return rates;
    }

    public List<Habit> findByUser(User user) {
        return habitRepository.findByUser(user);
    }
}