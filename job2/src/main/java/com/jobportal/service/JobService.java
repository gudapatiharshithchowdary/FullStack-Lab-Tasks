package com.jobportal.service;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    public List<Job> findAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> findJobsByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }

    public void saveJob(Job job) {
        jobRepository.save(job);
    }

    public Job findJobById(String id) {
        return jobRepository.findById(id).orElse(null);
    }

    public void deleteJob(String id) {
        jobRepository.deleteById(id);
    }
}
