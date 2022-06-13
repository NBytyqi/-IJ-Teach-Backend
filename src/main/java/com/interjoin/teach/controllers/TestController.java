package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.AvailableTimesStringDto;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.entities.AvailableTimes;
import com.interjoin.teach.entities.Curriculum;
import com.interjoin.teach.entities.Subject;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.repositories.CurriculumRepository;
import com.interjoin.teach.repositories.SubjectRepository;
import com.interjoin.teach.repositories.UserRepository;
import com.interjoin.teach.services.AvailableTimesService;
import com.interjoin.teach.services.UserService;
import com.interjoin.teach.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/datat")
@RequiredArgsConstructor
public class TestController {

    private final AvailableTimesService service;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final CurriculumRepository curriculumRepository;

    @PostMapping("/addsubjects")
    public void add(@RequestParam String curriculum, @RequestBody List<String> subjects) {

        for(String sub : subjects) {
            Optional<Subject> optional = subjectRepository.findBySubjectName(sub);
            Subject subject = null;
            if(optional.isPresent()) {
                subject = optional.get();
            } else {
                subject = subjectRepository.save(Subject.builder().subjectName(sub).build());
            }

            Curriculum curr = curriculumRepository.findFirstByCurriculumName(curriculum);
            curr.getSubjects().add(subject);
            curriculumRepository.save(curr);
        }

    }





    @GetMapping
    public List<AvailableTimesStringDto> test() {
        User user = userService.getUserByEmail("teacher2@gmail.com").get();
        List<AvailableTimes> availableTimes = service.findByUser(user);

        List<AvailableTimesStringDto> strings = DateUtils.map(availableTimes, "Europe/Belgrade");

        strings.forEach(availableTimesStringDto -> {
            availableTimesStringDto.setAvailableHourMinute(
                    availableTimesStringDto.getAvailableHourMinute().stream().filter(specificDate -> {
                        return specificDate.getDateTime().getDayOfWeek().toString().equals(availableTimesStringDto.getWeekDay().toUpperCase(Locale.ROOT));
                    }).collect(Collectors.toList())
            );

        });

        return strings;
    }

    @PostMapping("/available")
    public void test(@RequestBody UserSignupRequest request) {

        OffsetDateTime time = request.getAvailableTimes().get(0).getAvailableTimes().get(0);
//        map(time);
        map(OffsetDateTime.now());

//        List<AvailableTimes> av = AvailableTimesMapper.map(request.getAvailableTimes());
//        av = service.save(av);
//        User user = userService.getUserByEmail("bytyqinderim87@gmail.com").get();
//        user.setAvailableTimes(av);
//        userRepository.save(user);
//        System.out.println(user);
//
//        // testing
//        LocalDateTime firstAvailableTime = av.get(0).getDateTime();
//        ZoneOffset offset = ZoneOffset.of("-02:00");
//
//
//
//        System.out.println(firstAvailableTime.atOffset(offset).toLocalDateTime());
//        System.out.println(OffsetDateTime.of(firstAvailableTime.toLocalDateTime(), offset));

    }
    public void map(OffsetDateTime time) {
        String timezone = "Africa/Djibouti"; //+3:00
//        ZoneOffset offset = ZoneOffset.of(timezone);
        OffsetDateTime nowInAfrica = OffsetDateTime.from(time.atZoneSameInstant(ZoneId.of(timezone)));
        System.out.println(nowInAfrica);
    }


}
