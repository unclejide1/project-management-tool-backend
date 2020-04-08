package com.jide.ppmtool.controller;

import com.jide.ppmtool.MapValidationErrorService;
import com.jide.ppmtool.model.Project;
import com.jide.ppmtool.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {

    private ProjectService projectService;


    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    public ProjectController(MapValidationErrorService mapValidationErrorService, ProjectService projectService) {
        this.projectService = projectService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid  @RequestBody Project project, BindingResult result,
                                              Principal principal){

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationError(result);
        if(errorMap != null){
            return errorMap;
        }
        Project createdProject = projectService.saveOrUpdate(project, principal.getName());
        return  new ResponseEntity<Project>(createdProject, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectbyId(@PathVariable String projectId, Principal principal){
        Project foundProject = projectService.findByProjectId(projectId, principal.getName());

        return new ResponseEntity<>(foundProject, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<Project> getAllProjects(Principal principal){
        return projectService.findAllProjects(principal.getName());
    }


    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId, Principal principal){
        projectService.deleteProjectByIdentifier(projectId, principal.getName());

        return new ResponseEntity<>("Project with Id: '" +  projectId + "' was deleted", HttpStatus.OK);
    }
}
