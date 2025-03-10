package com.abrenica.contacts.controller;

import com.abrenica.contacts.service.GoogleContactsService;
import com.google.api.services.people.v1.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    private final GoogleContactsService googleContactsService;

    public UserController(GoogleContactsService googleContactsService) {
        this.googleContactsService = googleContactsService;
    }

    @GetMapping("/user-info")
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User principal){
        return principal.getAttributes();
    }

    @GetMapping("/add-contact")
    public String showAddContactForm() {
        return "create-contact";
    }

    @PostMapping("/contacts")
    public String createContact(@RequestParam String givenName,
                                @RequestParam String familyName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phoneNumber,
                                OAuth2AuthenticationToken authentication) throws Exception {
        googleContactsService.createContact(givenName, familyName, email, phoneNumber, authentication);
        return "redirect:/contacts";
    }

    @GetMapping("/contacts")
    public String showContacts(OAuth2AuthenticationToken authentication, Model model) throws Exception {
        List<Person> contacts = googleContactsService.getContacts(authentication);
        model.addAttribute("contacts", contacts);
        return "contacts";
    }

    @GetMapping("/edit-contact")
    public String editContact(@RequestParam String resourceName, Model model, OAuth2AuthenticationToken authentication) throws Exception {
        Person contact = googleContactsService.getContact(resourceName, authentication);
        model.addAttribute("contact", contact);
        return "edit-contact";
    }

    @PostMapping("/update-contact")
    public String updateContact(@RequestParam String resourceName,
                                @RequestParam String givenName,
                                @RequestParam String familyName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                OAuth2AuthenticationToken authentication) throws Exception {
        googleContactsService.updateContact(resourceName, givenName, familyName, email, phone, authentication);
        return "redirect:/contacts";
    }

    @PostMapping("/delete-contact")
    public String deleteContact(@RequestParam String resourceName, OAuth2AuthenticationToken authentication) throws Exception {
        googleContactsService.deleteContact(resourceName, authentication);
        return "redirect:/contacts";
    }
}
