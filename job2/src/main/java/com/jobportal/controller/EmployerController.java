package com.jobportal.controller;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User employer = userDetails.getUser();
        model.addAttribute("jobs", jobService.findJobsByEmployer(employer));
        return "employer/dashboard";
    }

    @GetMapping("/job/new")
    public String newJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "employer/post_job";
    }

    @PostMapping("/job")
    public String postJob(@ModelAttribute Job job, @AuthenticationPrincipal CustomUserDetails userDetails) {
        job.setEmployer(userDetails.getUser());
        jobService.saveJob(job);
        return "redirect:/employer/dashboard";
    }

    @GetMapping("/job/edit/{id}")
    public String editJobForm(@PathVariable String id, Model model) {
        model.addAttribute("job", jobService.findJobById(id));
        return "employer/edit_job";
    }

    @PostMapping("/job/update/{id}")
    public String updateJob(@PathVariable String id, @ModelAttribute Job updatedJob) {
        Job existing = jobService.findJobById(id);
        existing.setTitle(updatedJob.getTitle());
        existing.setDescription(updatedJob.getDescription());
        existing.setSkillsRequired(updatedJob.getSkillsRequired());
        existing.setSalary(updatedJob.getSalary());
        jobService.saveJob(existing);
        return "redirect:/employer/dashboard";
    }

    @GetMapping("/job/delete/{id}")
    public String deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);
        return "redirect:/employer/dashboard";
    }

    @GetMapping("/job/applicants/{id}")
    public String viewApplicants(@PathVariable String id, Model model) {
        Job job = jobService.findJobById(id);
        model.addAttribute("job", job);
        model.addAttribute("applications", applicationService.getApplicationsByJob(job));
        return "employer/applicants";
    }

    @GetMapping("/application/status/{id}/{status}")
    public String changeStatus(@PathVariable String id, @PathVariable String status) {
        applicationService.updateApplicationStatus(id, status);
        String jobId = applicationService.getApplicationById(id).getJob().getId();
        return "redirect:/employer/job/applicants/" + jobId;
    }

    @GetMapping("/application/shortlist/{id}")
    public String shortlistForm(@PathVariable String id, Model model) {
        com.jobportal.model.Application app = applicationService.getApplicationById(id);
        model.addAttribute("application", app);
        model.addAttribute("screeningDetails", new com.jobportal.model.ScreeningDetails());
        return "employer/shortlist_form";
    }

    @PostMapping("/application/shortlist/{id}")
    public String processShortlist(@PathVariable String id,
            @ModelAttribute com.jobportal.model.ScreeningDetails details) {
        com.jobportal.model.Application app = applicationService.getApplicationById(id);
        applicationService.updateApplicationStatus(id, "SHORTLISTED");

        String subject = "Update on your application for " + app.getJob().getTitle();
        String body = "Dear " + app.getApplicant().getName() + ",\n\n" +
                "Thank you for your interest in the " + app.getJob().getTitle() + " position at "
                + app.getJob().getEmployer().getName() + ".\n\n" +
                "We are pleased to inform you that your application has been shortlisted for the next stage of our hiring process. Based on your profile, we would like to invite you to participate in the screening round.\n\n"
                +
                "**Screening Details:**\n" +
                "Date: " + details.getDate() + "\n" +
                "Time: " + details.getTime() + "\n" +
                "Mode: " + details.getMode() + "\n" +
                "Platform/Location: " + details.getPlatformLocation() + "\n\n" +
                "During this round, we will evaluate your technical skills, problem-solving ability, and overall fit for the role.\n\n"
                +
                "Please confirm your availability by replying to this email. If you have any questions or require rescheduling, feel free to reach out.\n\n"
                +
                "We look forward to interacting with you and wish you the best for the upcoming round.\n\n" +
                "Best regards,\n" +
                app.getJob().getEmployer().getName() + "\n" +
                "Employer\n" +
                app.getJob().getEmployer().getName() + "\n" +
                details.getContactInformation();

        try {
            emailService.sendEmail(app.getApplicant().getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return "redirect:/employer/job/applicants/" + app.getJob().getId() + "?success=updated";
    }

    @GetMapping("/application/reject/{id}")
    public String rejectForm(@PathVariable String id, Model model) {
        com.jobportal.model.Application app = applicationService.getApplicationById(id);
        com.jobportal.model.RejectionDetails details = new com.jobportal.model.RejectionDetails();
        details.setCompanyName(app.getJob().getEmployer().getName());
        details.setYourName(app.getJob().getEmployer().getName());
        model.addAttribute("application", app);
        model.addAttribute("rejectionDetails", details);
        return "employer/reject_form";
    }

    @PostMapping("/application/reject/{id}")
    public String processReject(@PathVariable String id, @ModelAttribute com.jobportal.model.RejectionDetails details) {
        com.jobportal.model.Application app = applicationService.getApplicationById(id);
        applicationService.updateApplicationStatus(id, "REJECTED");

        String subject = "Update regarding your application for " + app.getJob().getTitle();
        String body = "Dear " + app.getApplicant().getName() + ",\n\n" +
                "Thank you for your interest in the " + app.getJob().getTitle() + " position at "
                + details.getCompanyName() + " and for taking the time to participate in our recruitment process.\n\n" +
                "We appreciate the effort you put into your application and the screening round. After careful consideration, we regret to inform you that we will not be moving forward with your application at this time.\n\n"
                +
                "This was a competitive process, and we had many strong candidates. We encourage you to apply for future opportunities that match your skills and experience.\n\n"
                +
                "We sincerely thank you for your interest in " + details.getCompanyName()
                + " and wish you all the best in your career ahead.\n\n" +
                "Best regards,\n" +
                details.getYourName() + "\n" +
                details.getYourPosition() + "\n" +
                details.getCompanyName();

        try {
            emailService.sendEmail(app.getApplicant().getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return "redirect:/employer/job/applicants/" + app.getJob().getId() + "?success=updated";
    }
}
