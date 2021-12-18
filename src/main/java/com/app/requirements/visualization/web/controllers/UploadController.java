package com.app.requirements.visualization.web.controllers;

import com.app.requirements.visualization.text.analyzer.AnalyticalUtility;
import com.app.requirements.visualization.text.analyzer.mappers.FileToMapMapper;
import com.app.requirements.visualization.text.analyzer.mappers.UserStoryFormMapper;
import com.app.requirements.visualization.web.dto.UserStoryFormDto;
import com.app.requirements.visualization.web.models.Requirements;
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

    FileToMapMapper fileToMapMapper = new FileToMapMapper();
    UserStoryFormMapper userStoryFormMapper = new UserStoryFormMapper();
    AnalyticalUtility analyticalUtility = new AnalyticalUtility();

    Map<String, String> userDictionary = new HashMap<>();
    Map<String, String> userStoryMap = new HashMap<>();
    String userStoryAll = "";

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
        return file.isEmpty() || !Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "txt");
    }

    @ModelAttribute("story")
    public UserStoryFormDto userStoryForm() {
        return new UserStoryFormDto();
    }

    @PostMapping(value = "/uploadStory")
    public ResponseEntity<?> uploadUserStory(@ModelAttribute("story") UserStoryFormDto userStoryFormDto) {
        userStoryMap.clear();
        userStoryAll = "";
        String errorMessage = analyticalUtility.isStoryCorrect(userStoryFormDto);
        if(errorMessage.isEmpty()) {
            userStoryMap = userStoryFormMapper.mapFormToMap(userStoryFormDto);
            userStoryAll = userStoryFormMapper.mapFormToString(userStoryFormDto);
            return ResponseEntity.ok("You successfully uploaded your story!");
        }
        return ResponseEntity.ok(errorMessage);
    }

    @PostMapping(value = "/startVisualize")
    public String startVisualization(RedirectAttributes redirectAttributes) throws IOException {
        Map<String, Set<String>> finalRequirements;
        if (userDictionary.isEmpty() || userStoryMap.isEmpty()) {
            userDictionary.clear();
            userStoryMap.clear();
            return FALSE;
        } else {
            finalRequirements = analyticalUtility.startAnalysis(userStoryMap, userDictionary, userStoryAll);
            Requirements requirements = new Requirements();
            requirements.setStringList(finalRequirements.get("text"));
            requirements.setColumnList(finalRequirements.get("columns"));
            requirements.setUIList(finalRequirements.get("ui"));
            requirements.setKeyPhrases(finalRequirements.get("keyPhrases"));
            redirectAttributes.addFlashAttribute("requirement", requirements);
            return "redirect:/result";
        }

    }
}



