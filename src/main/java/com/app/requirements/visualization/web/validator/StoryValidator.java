package com.app.requirements.visualization.web.validator;

import com.app.requirements.visualization.web.dto.UserStoryFormDto;
import org.apache.commons.lang3.StringUtils;

public class StoryValidator {

    public String isStoryCorrect(UserStoryFormDto userStoryFormDto) {
        String finalMessage = "";
        String tempPersona = userStoryFormDto.getPersona();
        String tempAction = userStoryFormDto.getAction();
        String tempBenefit = userStoryFormDto.getBenefit();

        if(tempPersona.isEmpty() || tempAction.isEmpty() || tempBenefit.isEmpty()) {
            return "Some fields are missing, please try again...";
        }

        if(!StringUtils.isAlpha(tempPersona.replace(" ", "")) ||
                !StringUtils.isAlpha(tempAction.replace(" ", "")) ||
                !StringUtils.isAlpha(tempBenefit.replace(" ", ""))){
            return "Text contains some special character which are not allowed. Please try again...";
        }

        if(!StringUtils.isAlpha(tempPersona.replace(" ", ""))) {
            if(finalMessage.isEmpty())
                finalMessage = finalMessage + "Too many numbers in the first field";
            else
                finalMessage = finalMessage + "<br/>" + "Too many numbers in the first field";
        }
        if(StringUtils.isNumeric(tempAction.replace(" ", ""))) {
            if(finalMessage.isEmpty())
                finalMessage = finalMessage + "Too many numbers in the second field";
            else
                finalMessage = finalMessage + "<br/>" + "Too many numbers in the second field";
        }
        if(StringUtils.isNumeric(tempBenefit.replace(" ", ""))) {
            if(finalMessage.isEmpty())
                finalMessage = finalMessage + "Too many numbers in the third field";
            else
                finalMessage = finalMessage + "<br/>" + "Too many numbers in the third field";
        }

        return finalMessage;

    }

    public void prepareStory(UserStoryFormDto userStoryFormDto) {
        userStoryFormDto.setPersona(userStoryFormDto.getPersona().trim());
        userStoryFormDto.setAction(userStoryFormDto.getAction().trim());
        userStoryFormDto.setBenefit(userStoryFormDto.getBenefit().trim());
    }
}
