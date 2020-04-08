package com.jide.ppmtool.services;


import com.jide.ppmtool.exceptions.ProjectNotFoundException;
import com.jide.ppmtool.model.Backlog;
import com.jide.ppmtool.model.Project;
import com.jide.ppmtool.model.ProjectTask;
import com.jide.ppmtool.respositories.BackLogRepository;
import com.jide.ppmtool.respositories.ProjectRepository;
import com.jide.ppmtool.respositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    private BackLogRepository backLogRepository;

    private ProjectTaskRepository projectTaskRepository;

    private ProjectRepository projectRepository;

    private ProjectService projectService;


    @Autowired
    public ProjectTaskService(BackLogRepository backLogRepository, ProjectTaskRepository projectTaskRepository,
                              ProjectRepository projectRepository, ProjectService projectService) {
        this.backLogRepository = backLogRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username){
            Backlog backlog = projectService.findByProjectId(projectIdentifier, username).getBacklog(); //backLogRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());
            System.out.println(backlog.toString() +"====");
            projectTask.setBacklog(backlog);
            Integer backlogSequence = backlog.getPTSequence();
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);
            projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);
            if(projectTask.getStatus() == null || projectTask.getStatus().equals("")){
                projectTask.setStatus("PENDING");
            }
            if(projectTask.getPriority() == null || projectTask.getPriority() == 0){
                projectTask.setPriority(3);
            }
      return projectTaskRepository.save(projectTask);


    }

    public Iterable<ProjectTask> findBacklogById(String backlog_id, String username) {
        projectService.findByProjectId(backlog_id, username);

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlog_id);
    }

    public ProjectTask findProjectTaskBySequence(String backlog_id, String sequence, String username){
        projectService.findByProjectId(backlog_id, username);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(sequence);
        if(projectTask == null){
            throw new ProjectNotFoundException("project Task with id:'" + sequence + "' not found");
        }
        if(!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("project Task with id:'" + sequence + "' does not exist in project: '" + backlog_id +"'");
        }

        return  projectTask;
    }

    public ProjectTask updateProjectTaskByProjectSequence(ProjectTask updatedProjectTask, String backlog_id, String projectSequence,
                                                          String username ){
        ProjectTask projectTask =  findProjectTaskBySequence(backlog_id, projectSequence, username);

        projectTask = updatedProjectTask;

        return projectTaskRepository.save(projectTask);

    }

    public void deleteProjectTaskByProjectSequence(String backlog_id, String projectSequence, String username){
        ProjectTask projectTask =  findProjectTaskBySequence(backlog_id, projectSequence, username);
        projectTaskRepository.delete(projectTask);

    }
}
