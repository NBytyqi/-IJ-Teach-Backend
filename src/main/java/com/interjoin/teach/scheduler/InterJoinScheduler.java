package com.interjoin.teach.scheduler;

import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class InterJoinScheduler {

    private final UserService userService;

//    @Scheduled(cron = "0 0 0 1/6 * ? *")
//    @Scheduled(fixedRate = 1000 * 60)
    public void scheduleS3UrlGenerationWeekly() {
//        System.out.println("Running scheduler");
//        userService.generateProfilePicturesAndExperiencesPresignedUrls();
//        System.out.println("Generating presigned urls done");
    }
}
