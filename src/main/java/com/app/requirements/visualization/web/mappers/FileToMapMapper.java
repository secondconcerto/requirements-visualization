package com.app.requirements.visualization.web.mappers;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class FileToMapMapper {
    public Map<String, String> mapFile(MultipartFile file) throws IOException {
        InputStream inputStream = new BufferedInputStream(file.getInputStream());
        Map<String, String> map = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .map(String::toLowerCase)
                .collect(Collectors.toMap(k -> k.split(":")[0], v -> v.split(":")[1]));
        inputStream.close();
        return map;
    }
}
