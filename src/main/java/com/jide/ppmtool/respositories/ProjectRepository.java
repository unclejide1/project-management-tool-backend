package com.jide.ppmtool.respositories;


import com.jide.ppmtool.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    Project findByProjectIdentifier(String projectIdentifier);
    @Override
    Iterable<Project> findAll();

    Iterable<Project> findByProjectLeader(String username);
}
