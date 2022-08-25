package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.ExperienceDto;
import com.interjoin.teach.entities.Experience;
import com.interjoin.teach.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ExperienceMapper {

    public static ExperienceDto map(Experience experience) {
        return ExperienceDto.builder()
                .id(experience.getId())
                .companyName(experience.getCompanyName())
                .title(experience.getTitle())
                .from(experience.getFrom())
                .to(experience.getTo())
                .base64Logo(experience.getBase64Logo())
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
                .companyName(experience.getCompanyName())
                .title(experience.getTitle())
                .from(experience.getFrom())
                .to(experience.getTo())
                .base64Logo(experience.getBase64Logo())
                .user(forUser)
                .description(experience.getDescription())
//                .logo(
//                        Optional.ofNullable(experience.getLogo()).map(prf ->
//                                        Arrays.copyOf( experience.getLogo(), experience.getLogo().length ))
//                                .orElse(null)
//                )
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
}
