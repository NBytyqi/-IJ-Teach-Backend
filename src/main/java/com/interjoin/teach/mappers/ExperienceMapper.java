package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.ExperienceDto;
import com.interjoin.teach.entities.Experience;
import com.interjoin.teach.entities.User;

import java.util.*;

public class ExperienceMapper {

    public static ExperienceDto map(Experience experience) {
        return ExperienceDto.builder()
                .id(experience.getId())
                .uuid(experience.getUuid())
                .companyName(experience.getCompanyName())
                .title(experience.getTitle())
                .from(experience.getFrom())
                .to(experience.getTo())
                .awsLogoUrl(experience.getAwsLogoUrl())
                .awsLogoRef(experience.getAwsLogoRef())
                .description(experience.getDescription())
//                .logo(
//                        Optional.ofNullable(experience.getLogo()).map(prf ->
//                                        Arrays.copyOf( experience.getLogo(), experience.getLogo().length ))
//                                .orElse(null)
//                )
                .build();
    }

    public static Experience map(ExperienceDto experience, User forUser) {
        return Experience.builder()
                .id(experience.getId())
                .uuid(Optional.ofNullable(experience.getUuid()).orElse(UUID.randomUUID().toString()))
                .companyName(experience.getCompanyName())
                .title(experience.getTitle())
                .from(experience.getFrom())
                .to(experience.getTo())
                .awsLogoUrl(experience.getAwsLogoUrl())
                .awsLogoRef(experience.getAwsLogoRef())
                .user(forUser)
                .description(experience.getDescription())

                .build();
    }

    public static List<ExperienceDto> mapList(List<Experience> experiences) {
        List<ExperienceDto> exps = new ArrayList<>();

        for (Experience ex : experiences) {
            exps.add(map(ex));
        }
        return exps;
    }

    public static List<Experience> map(List<ExperienceDto> experiences, User forUser) {
        List<Experience> exps = new ArrayList<>();

        for (ExperienceDto ex : experiences) {
            exps.add(map(ex, forUser));
        }
        return exps;
    }

//    public static List<Experience> mapForSave(List<ExperienceDto> experiences, User forUser) {
//        List<Experience> experiencess = new ArrayList<>();
//        for(ExperienceDto experience : experiences) {
//            experiencess.add(
//                    Experience.builder()
//                            .id(experience.getId())
//                            .uuid(Optional.ofNullable(experience.getUuid()).orElse(UUID.randomUUID().toString()))
//                            .companyName(experience.getCompanyName())
//                            .title(experience.getTitle())
//                            .from(experience.getFrom())
//                            .to(experience.getTo())
//                            .user(forUser)
//                            .description(experience.getDescription())
//                            .build();
//            )
//        }
//    }
}
