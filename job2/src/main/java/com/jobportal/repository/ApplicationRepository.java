package com.jobportal.repository;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ApplicationRepository extends MongoRepository<Application, String> {
    List<Application> findByJob(Job job);

    List<Application> findByApplicant(User applicant);

    boolean existsByJobAndApplicant(Job job, User applicant);
}
