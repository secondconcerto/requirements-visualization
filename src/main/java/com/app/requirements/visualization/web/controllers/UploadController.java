package com.app.requirements.visualization.web.controllers;

import com.app.requirements.visualization.text.analyzer.AnalyticalUtility;
import com.app.requirements.visualization.text.analyzer.mappers.FileToMapMapper;
import com.app.requirements.visualization.text.analyzer.mappers.UserStoryFormMapper;
import com.app.requirements.visualization.web.dto.UserStoryFormDto;
import com.app.requirements.visualization.web.models.Requirements;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class UploadController {

    private static final String FALSE = "redirect:/visualize?false";

    FileToMapMapper fileToMapMapper = new FileToMapMapper();
    UserStoryFormMapper userStoryFormMapper = new UserStoryFormMapper();
    AnalyticalUtility analyticalUtility = new AnalyticalUtility();

    Map<String, String> userDictionary = new HashMap<>();
    Map<String, String> userStoryMap = new HashMap<>();
    String userStoryAll = "";

    public UploadController() {
        super();
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {

        if (validate(file)) {
            attributes.addFlashAttribute("message", "Please select a txt file to upload.");
            return "redirect:/visualize";
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            userDictionary.clear();
            userDictionary = fileToMapMapper.mapFile(file);
        } catch (IOException exception) {
            attributes.addFlashAttribute("message", "Unfortunately, file has a wrong format. Try again.");
            return "redirect:/visualize";
        }
        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        return "redirect:/visualize";
    }

    private boolean validate(MultipartFile file) {
        return file.isEmpty() || !Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "txt");
    }

    @ModelAttribute("story")
    public UserStoryFormDto userStoryForm() {
        return new UserStoryFormDto();
    }

    @PostMapping(value = "/uploadStory")
    public String uploadUserStory(@ModelAttribute("story") UserStoryFormDto userStoryFormDto) {
        userStoryMap = userStoryFormMapper.mapFormToMap(userStoryFormDto);
        userStoryAll = userStoryFormMapper.mapFormToString(userStoryFormDto);
        return "redirect:/visualize";
    }

    @PostMapping(value = "/startVisualize")
    public String startVisualization(RedirectAttributes redirectAttributes) throws IOException {
        Map<String, List<String>> finalRequirements;
        if (userDictionary.isEmpty() || userStoryMap.isEmpty()) {
            return FALSE;
        } else {
            finalRequirements = analyticalUtility.startAnalysis(userStoryMap, userDictionary, userStoryAll);
            Requirements requirements = new Requirements();
            requirements.setStringList(finalRequirements.get("text"));
            requirements.setColumnList(finalRequirements.get("columns"));
            redirectAttributes.addFlashAttribute("requirement", requirements);
            return "redirect:/result";
        }

    }
}