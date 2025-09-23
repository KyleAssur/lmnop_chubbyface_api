package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Support;
import za.ac.cput.service.SupportService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class SupportController {

    private final SupportService contactSupportService;

    @Autowired
    public SupportController(SupportService contactSupportService) {
        this.contactSupportService = contactSupportService;
    }

    @PostMapping("/contact-support")
    public Support submitContactSupport(@RequestBody Support contactSupport) {
        return contactSupportService.create(contactSupport);
    }

    @GetMapping("/ping")
    public String ping() {
        return "Backend is running and accessible!";
    }

    @GetMapping("/allSupportMessages")
    public List<Support> getAllSupportMessages() {
        return contactSupportService.getAll();
    }

    @GetMapping("/read/{id}")
    public Support readContactSupport(@PathVariable Long id) {
        return contactSupportService.read(id);
    }

    @PostMapping("/update")
    public Support updateContactSupport(@RequestBody Support contactSupport) {
        return contactSupportService.update(contactSupport);
    }
}
