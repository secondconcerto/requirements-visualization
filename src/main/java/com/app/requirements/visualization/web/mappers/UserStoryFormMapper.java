package com.app.requirements.visualization.web.mappers;

import com.app.requirements.visualization.web.dto.UserStoryFormDto;

import java.util.HashMap;
import java.util.Map;

public class UserStoryFormMapper {

    private final String PERSONA = "Persona";
    private final String ACTION = "Action";
    private final String BENEFIT = "Benefit";

    public Map<String, String> mapFormToMap(UserStoryFormDto userStoryFormDto) {
        return new HashMap<String, String>() {{
            put(PERSONA, userStoryFormDto.getPersona());
            put(ACTION, userStoryFormDto.getAction());
            put(BENEFIT, userStoryFormDto.getBenefit());
        }};
    }

    public String mapFormToString(UserStoryFormDto userStoryFormDto) {
        return "As a " + userStoryFormDto.getPersona() + ", I want to " + userStoryFormDto.getAction()
                + ", so that  " + userStoryFormDto.getBenefit();
    }

}
