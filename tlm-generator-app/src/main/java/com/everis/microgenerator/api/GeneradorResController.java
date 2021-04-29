package com.everis.microgenerator.api;

import com.everis.microgenerator.model.GeneratorConfig;
import com.everis.microgenerator.service.TemplateGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/generadorapi")
public class GeneradorResController {
    private TemplateGenerator templateGenerator = new TemplateGenerator();
    private final GeneratorConfig generatorConfig;

    public GeneradorResController(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    @GetMapping("")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public GeneratorConfig listTemplates() {
        return generatorConfig;
    }

    @PostMapping(value = "/generator")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<byte[]> getProject(@RequestBody Map<String, String> properties) throws Exception {
        log.info("properties: {}", properties);
        File sbtJar = new File("./sbt-launch.jar");
        ArrayList<String> params = new ArrayList<>();
        String name = "template";
        params.add("java");
        params.add("-jar");
        params.add(sbtJar.getAbsolutePath());
        params.add("new");
        params.add(properties.get("gitUrl"));
        properties.keySet().removeIf(key -> key.equals("gitUrl"));
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (!entry.getValue().equals(""))
                params.add("--" + entry.getKey() + "=" + entry.getValue());
            name = entry.getKey().equals("name") ? entry.getValue() : name;
        }
        log.info("cmd: {}", String.join(" ", params));

        byte[] response = templateGenerator.getTemplate(params, name);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("charset", "utf-8");
        responseHeaders.setContentType(MediaType.valueOf("application/zip"));
        responseHeaders.setContentLength(response.length);
        responseHeaders.set("Content-disposition", "attachment; filename=\"" + name + ".zip\"");

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
    }
}
