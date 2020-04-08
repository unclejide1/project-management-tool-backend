package com.jide.ppmtool.controller;

import com.jide.ppmtool.MapValidationErrorService;
import com.jide.ppmtool.model.Project;
import com.jide.ppmtool.model.ProjectTask;
import com.jide.ppmtool.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/project-task")
public class ProjectTaskController {

    private ProjectTaskService projectTaskService;

    private MapValidationErrorService  mapValidationErrorService;

    @Autowired
    public ProjectTaskController(ProjectTaskService projectTaskService, MapValidationErrorService mapValidationErrorService) {
        this.projectTaskService = projectTaskService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("/{backlog_id}")
    public ResponseEntity<?> addProjectTaskToBacklog(@Valid @RequestBody ProjectTask projectTask, BindingResult result,
                                                     @PathVariable String backlog_id, Principal principal){

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationError(result);
        if(errorMap != null){
            return errorMap;
        }

        ProjectTask newProjectTask = projectTaskService.addProjectTask(backlog_id, projectTask, principal.getName());
        return  new ResponseEntity<>(newProjectTask, HttpStatus.CREATED);

    }

    @GetMapping("/{backlog_id}")
    public Iterable<ProjectTask> getProjectBacklog(@PathVariable String backlog_id, Principal principal){
        return (projectTaskService.findBacklogById(backlog_id, principal.getName()));
    }

    @GetMapping("/{backlog_id}/{pt_sequence}")
    public ResponseEntity<?>  getProjectTask(@PathVariable String backlog_id, @PathVariable String pt_sequence, Principal principal){
        ProjectTask foundProjectTask = projectTaskService.findProjectTaskBySequence(backlog_id, pt_sequence, principal.getName());
        return  new ResponseEntity<ProjectTask>(foundProjectTask, HttpStatus.OK);
    }

    @PatchMapping("/{backlog_id}/{pt_sequence}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask updatedTask, BindingResult result,
                                                         @PathVariable String backlog_id,
                                                         @PathVariable String pt_sequence,
                                               Principal principal){

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationError(result);
        if(errorMap != null){
            return errorMap;
        }
        ProjectTask updatedProjectTask = projectTaskService.updateProjectTaskByProjectSequence(updatedTask,
                backlog_id,pt_sequence, principal.getName() );

        return  new ResponseEntity<ProjectTask>(updatedProjectTask, HttpStatus.OK);
    }

    @DeleteMapping("/{backlog_id}/{pt_sequence}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String backlog_id,
                                               @PathVariable String pt_sequence,
                                               Principal principal){

        projectTaskService.deleteProjectTaskByProjectSequence(backlog_id,pt_sequence, principal.getName());
        return  new ResponseEntity<String>("ProjectTask: " + pt_sequence + " was deleted successfully", HttpStatus.OK);
    }

}
