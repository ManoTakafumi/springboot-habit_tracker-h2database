package com.example.habit_tracker.controller;

import com.example.habit_tracker.entity.Habit;
import com.example.habit_tracker.entity.Record;
import com.example.habit_tracker.entity.User;
import com.example.habit_tracker.service.HabitService;
import com.example.habit_tracker.repository.HabitRepository;
import com.example.habit_tracker.service.RecordService;
import com.example.habit_tracker.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
public class HabitController {

    private final HabitService habitService;
    private final RecordService recordService;
    private final UserService userService;
    private final HabitRepository habitRepository;

    public HabitController(HabitService habitService,
                           RecordService recordService,
                           UserService userService,
                           HabitRepository habitRepository) {
        this.habitService = habitService;
        this.recordService = recordService;
        this.userService = userService;
        this.habitRepository = habitRepository;
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Habit> habits = habitService.getHabits(user);

        Map<Long, Double> achievementRates = habitService.calculateAchievementRates(habits);

        List<Map<String, Object>> habitStats = habits.stream().map(habit -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("id", habit.getId());
            stat.put("name", habit.getName());
            stat.put("rate", achievementRates.getOrDefault(habit.getId(), 0.0));
            return stat;
        }).collect(Collectors.toList());

        model.addAttribute("habits", habits);
        model.addAttribute("habitStats", habitStats);
        model.addAttribute("username", user.getUsername());

        return "habits";
    }

    @PostMapping("/habit")
    public String addHabit(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam String name) {
        User user = userService.findByUsername(userDetails.getUsername());
        habitService.addHabit(name, user);
        return "redirect:/";
    }

    @GetMapping("/habits")
    public String showHabits(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Habit> habits = habitService.findByUser(user);

        Map<Long, Double> achievementRates = habitService.calculateAchievementRates(habits);

        // グラフ用に変換
        List<Map<String, Object>> habitStats = habits.stream().map(habit -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("id", habit.getId());
            stat.put("name", habit.getName());
            stat.put("rate", achievementRates.getOrDefault(habit.getId(), 0.0));
            return stat;
        }).collect(Collectors.toList());

        model.addAttribute("habits", habits);
        model.addAttribute("habitStats", habitStats);
        model.addAttribute("username", user.getUsername());

        return "habits";
    }

    @PostMapping("/record")
    public String addRecord(@RequestParam Long habitId,
                            @RequestParam boolean completed) {
        Habit habit = habitService.findById(habitId)
            .orElseThrow(() -> new IllegalArgumentException("Habit not found"));
        recordService.markRecord(habit, LocalDate.now(), completed);
        return "redirect:/records/" + habitId;
    }

    @GetMapping("/records/{habitId}")
    public String showRecords(@PathVariable Long habitId,
                              @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        Habit habit = habitService.findById(habitId)
            .orElseThrow(() -> new IllegalArgumentException("Habit not found"));
        List<Record> records = recordService.getRecords(habit);

        long total = records.size();
        long completed = records.stream().filter(Record::isCompleted).count();
        long notCompleted = total - completed;

        model.addAttribute("records", records);
        model.addAttribute("habit", habit);
        model.addAttribute("username", user.getUsername());

        //グラフ用データ
        model.addAttribute("total", total);
        model.addAttribute("completed", completed);
        model.addAttribute("notCompleted", notCompleted);
        
        return "records";
    }

    @PostMapping("/habit/delete/{id}")
    public String deleteHabit(@PathVariable Long id, Principal principal) {
        //ログインユーザーを取得
        User user = userService.findByUsername(principal.getName());
        Habit habit = habitRepository.findById(id).orElse(null);

        if (habit != null && habit.getUser().equals(user)) {
            habitRepository.delete(habit);
        }
        
        return "redirect:/";
    }
}