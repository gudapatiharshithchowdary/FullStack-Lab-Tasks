package com.jobportal.service;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserService userService;

    public void applyForJob(Job job, User applicant) {
        if (!applicationRepository.existsByJobAndApplicant(job, applicant)) {
            Application app = new Application();
            app.setJob(job);
            app.setApplicant(applicant);
            app.setStatus("PENDING");
            applicationRepository.save(app);
        }
    }

    public List<Application> getApplicationsByApplicant(User applicant) {
        return applicationRepository.findByApplicant(applicant);
    }

    public List<Application> getApplicationsByJob(Job job) {
        return applicationRepository.findByJob(job);
    }

    public Application getApplicationById(String id) {
        return applicationRepository.findById(id).orElse(null);
    }

    public void updateApplicationStatus(String id, String status) {
        Application app = getApplicationById(id);
        if (app != null) {
            app.setStatus(status);
            applicationRepository.save(app);

            // Update candidate profile status
            User applicant = app.getApplicant();
            applicant.setStatus(status);
            userService.updateUser(applicant);
        }
    }
}
