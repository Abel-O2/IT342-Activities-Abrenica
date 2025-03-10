package com.abrenica.contacts.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PeopleConfig {
    public PeopleService peopleService(Credential credential) throws Exception {
        return new PeopleService.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Abrenica Oauth2").build();
    }
}
