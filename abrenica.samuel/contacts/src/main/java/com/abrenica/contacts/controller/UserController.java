package com.abrenica.contacts.controller;

import com.abrenica.contacts.service.GooglePeopleService;
import com.google.api.services.people.v1.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    public GooglePeopleService googlePeopleService;
    @GetMapping("/user-info")
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User principal){
        return principal.getAttributes();
    }

    @GetMapping("/user-contacts")
    public List<Person> getContacts() throws Exception {
        return googlePeopleService.getContacts();
    }
}
