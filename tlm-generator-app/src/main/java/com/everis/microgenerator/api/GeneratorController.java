package com.everis.microgenerator.api;

import com.everis.microgenerator.model.Repo;
import com.everis.microgenerator.model.Template;
import com.everis.microgenerator.service.TemplateGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api")
public class GeneratorController {

    private TemplateGenerator templateGenerator = new TemplateGenerator();
    private Repo repository = new Repo();

    @GetMapping("")
    public String init(@RequestParam(value = "id", defaultValue = "0") String id, Model model) {
        log.info("init(id: {}, model: {})", id, model);
        ArrayList<Repo> listRepo;
        listRepo = templateGenerator.getRepository();
        listRepo.forEach(re -> {
            if (re.id.equals(id)) {
                repository = re;
            }
        });
        model.addAttribute("repo", repository);
        model.addAttribute("listRepo", listRepo);
        model.addAttribute("form", templateGenerator.getParams(repository));
        model.addAttribute("listDep", templateGenerator.getDependencias(repository));
        log.info("model updated: {}", model);
        return "generator";
    }

    @PostMapping(value = "/generator", produces = "application/zip")
    public ResponseEntity<byte[]> getProject(@ModelAttribute("form") Template form) throws Exception {
        log.info("form: {}", form);
        File sbtJar = new File("/sbt-launch.jar");
        ArrayList<String> params = new ArrayList<>();
        String name = "";
        params.add("java");
        params.add("-jar");
        params.add(sbtJar.getAbsolutePath());
        params.add("new");
        params.add(repository.url);
        for (Map.Entry<String, String> entry : form.getProperties().entrySet()) {
            if (!entry.getValue().equals(""))
                params.add("--" + entry.getKey() + "=" + entry.getValue());
            name = entry.getKey().equals("name") ? entry.getValue() : name;
        }

        byte[] r = templateGenerator.getTemplate(params, name);
        return ResponseEntity.created(new URI("api/generator"))
                .header("Content-Type", "application/octet-stream")
                .header("Content-Disposition", "attachment; filename=\"" + name + ".zip\"")
                .body(r);
    }
}
