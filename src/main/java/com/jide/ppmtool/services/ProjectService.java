package com.jide.ppmtool.services;

import com.jide.ppmtool.exceptions.ProjectIdException;
import com.jide.ppmtool.exceptions.ProjectNotFoundException;
import com.jide.ppmtool.model.Backlog;
import com.jide.ppmtool.model.Project;
import com.jide.ppmtool.model.User;
import com.jide.ppmtool.respositories.BackLogRepository;
import com.jide.ppmtool.respositories.ProjectRepository;
import com.jide.ppmtool.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {


    private ProjectRepository projectRepository;


    private BackLogRepository backLogRepository;

    private UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, BackLogRepository backLogRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.backLogRepository = backLogRepository;
        this.userRepository = userRepository;
    }

    public Project saveOrUpdate(Project project, String username) {

        if(project.getId() != null){
            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());

            if(existingProject != null && (!existingProject.getProjectLeader().equals(username))){
                throw  new ProjectNotFoundException("Project Not found in your account");
            }else if(existingProject == null){
                throw new ProjectNotFoundException("Project with Id: '" + project.getProjectIdentifier() + "' cannot be updated " +
                        "because it does not exist");
            }
        }

        try{
            User user = userRepository.findByUsername(username);

            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            if(project.getId() == null){
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                project.setCreatedAt(project.getCreatedAt());
                project.setUpdatedAt(project.getUpdatedAt());
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            if(project.getId() != null){
                project.setBacklog(backLogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
                project.setCreatedAt(project.getCreatedAt());
            }
            return  projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdException("Project Id '" + project.getProjectIdentifier().toUpperCase() +"' already exists");
        }

    }

    public  Project findByProjectId(String projectId, String username){
        Project foundProject = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(foundProject == null){
            throw new ProjectIdException("Project with " + projectId +" does not exist");
        }

        if(!foundProject.getProjectLeader().equals(username)){
            throw new ProjectNotFoundException("Project does not exist in your account");
        }

        return  foundProject;
    }

    public Iterable<Project> findAllProjects( String username){

        return projectRepository.findByProjectLeader(username);
    }

    public void deleteProjectByIdentifier (String projectId, String username){

        projectRepository.delete(findByProjectId(projectId, username));
    }
}
