/*
package com.app.requirements.visualization.web;

import com.app.requirements.visualization.web.dto.UserStoryForm;
import com.app.requirements.visualization.text.analyzer.mappers.UserStoryFormToMapMapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/visualize")
public class UserStoryController {

    Map<String, String> userStoryMap;
    UserStoryFormToMapMapper userStoryFormToMapMapper = new UserStoryFormToMapMapper();

    public UserStoryController() {
        super();
    }

    @ModelAttribute("story")
    public UserStoryForm userStoryForm() {
        return new UserStoryForm();
    }


    @PostMapping("/uploadStory")
    public void uploadUserStory(@ModelAttribute("story") UserStoryForm story, BindingResult bindingResult) {
        userStoryMap = userStoryFormToMapMapper.mapFormToMap(story);
    }
}
*/
