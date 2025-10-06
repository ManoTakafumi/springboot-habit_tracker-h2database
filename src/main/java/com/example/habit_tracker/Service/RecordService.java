package com.example.habit_tracker.service;

import com.example.habit_tracker.entity.Record;
import com.example.habit_tracker.entity.Habit;
import com.example.habit_tracker.repository.RecordRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecordService {
    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public Record markRecord(Habit habit, LocalDate date, boolean completed) {
        Record record = recordRepository.findByHabitAndDate(habit, date);
        if (record == null) {
            record = new Record(date, completed, habit);
        } else {
            record.setCompleted(completed);
        }
        return recordRepository.save(record);
    }

    public List<Record> getRecords(Habit habit) {
        return recordRepository.findByHabit(habit);
    }
}