package com.jobportal.controller;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    private final String UPLOAD_DIR = "uploads/";

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userService.findByUsername(userDetails.getUsername()));
        model.addAttribute("applications", applicationService.getApplicationsByApplicant(userDetails.getUser()));
        return "student/dashboard";
    }

    @PostMapping("/uploadResume")
    public String uploadResume(@RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                User user = userService.findByUsername(userDetails.getUsername());
                user.setResumePath(filename);
                userService.updateUser(user);

                userDetails.getUser().setResumePath(filename);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/jobs")
    public String browseJobs(Model model) {
        model.addAttribute("jobs", jobService.findAllJobs());
        return "student/jobs";
    }

    @GetMapping("/job/apply/{id}")
    public String applyForJob(@PathVariable String id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Job job = jobService.findJobById(id);
        User user = userService.findByUsername(userDetails.getUsername());

        if (user.getResumePath() == null || user.getResumePath().isEmpty()) {
            return "redirect:/student/dashboard?error=resume";
        }

        applicationService.applyForJob(job, user);
        return "redirect:/student/jobs?success=applied";
    }
}
