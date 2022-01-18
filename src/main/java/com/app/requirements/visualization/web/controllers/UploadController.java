package com.app.requirements.visualization.web.controllers;

import com.app.requirements.visualization.text.analyzer.AnalyticalUtility;
import com.app.requirements.visualization.web.mappers.FileToMapMapper;
import com.app.requirements.visualization.web.mappers.UserStoryFormMapper;
import com.app.requirements.visualization.web.dto.UserStoryFormDto;
import com.app.requirements.visualization.web.models.Requirements;
import com.app.requirements.visualization.web.validator.StoryValidator;
import org.apache.commons.io.FilenameUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;


@Controller
public class UploadController {

    private static final String FALSE = "redirect:/visualize?false";

    private FileToMapMapper fileToMapMapper = new FileToMapMapper();
    private UserStoryFormMapper userStoryFormMapper = new UserStoryFormMapper();
    private AnalyticalUtility analyticalUtility = new AnalyticalUtility();
    private StoryValidator storyValidator = new StoryValidator();

    private Map<String, String> userDictionary = new HashMap<>();
    private Map<String, String> userStoryMap = new HashMap<>();
    private String userStoryAll = "";

    public UploadController() {
        super();
    }

    @PostMapping(value = {"/uploadFile"}, consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        userDictionary.clear();
        if (validate(file)) {
            return ResponseEntity.ok("Please select a TXT file to upload.");
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            userDictionary = fileToMapMapper.mapFile(file);
        } catch (IOException exception) {
            return ResponseEntity.ok("Unfortunately, there was a problem with loading chosen file. Try again with a different file.");
        }
        return ResponseEntity.ok("You successfully uploaded " + fileName + '!');
    }

    private boolean validate(MultipartFile file) {
        return !Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "txt");
    }

    @ModelAttribute("story")
    public UserStoryFormDto userStoryForm() {
        return new UserStoryFormDto();
    }

    @PostMapping(value = "/uploadStory")
    public ResponseEntity<?> uploadUserStory(@ModelAttribute("story") UserStoryFormDto userStoryFormDto) {
        userStoryMap.clear();
        userStoryAll = "";
        String errorMessage = storyValidator.isStoryCorrect(userStoryFormDto);
        if(errorMessage.isEmpty()) {
            storyValidator.prepareStory(userStoryFormDto);
            userStoryMap = userStoryFormMapper.mapFormToMap(userStoryFormDto);
            userStoryAll = userStoryFormMapper.mapFormToString(userStoryFormDto);
            return ResponseEntity.ok("You successfully uploaded your story!");
        }
        return ResponseEntity.ok(errorMessage);
    }

    @PostMapping(value = "/startVisualize")
    public String startVisualization(RedirectAttributes redirectAttributes) throws IOException {
        Map<String, Set<String>> finalRequirements;
        if (userStoryMap.isEmpty()) {
            userDictionary.clear();
            userStoryMap.clear();
            return FALSE;
        } else {
            try {
                finalRequirements = analyticalUtility.startAnalysis(userStoryMap, userDictionary, userStoryAll);
                Requirements requirements = new Requirements();
                requirements.setStringList(finalRequirements.get("text"));
                requirements.setColumnList(finalRequirements.get("columns"));
                requirements.setUIList(finalRequirements.get("ui"));
                requirements.setKeyPhrases(finalRequirements.get("keyPhrases"));
                if (requirements.getStringList().isEmpty() && requirements.getColumnList().isEmpty()
                        && requirements.getKeyPhrases().isEmpty() && requirements.getUIList().isEmpty()) {
                    requirements.setStringList(new HashSet<>(Collections.singletonList("No requirements were found :( ")));
                }
                redirectAttributes.addFlashAttribute("requirement", requirements);
            } catch (Exception e){
                e.toString();
                e.printStackTrace();
            }
            return "redirect:/result";
        }

    }
}



