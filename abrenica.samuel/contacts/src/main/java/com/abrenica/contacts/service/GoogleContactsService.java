package com.abrenica.contacts.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GoogleContactsService {

    private final GoogleCredentialService googleCredentialService;

    public GoogleContactsService(GoogleCredentialService googleCredentialService) {
        this.googleCredentialService = googleCredentialService;
    }

    // Initialize PeopleService instance
    private PeopleService buildPeopleService(OAuth2AuthenticationToken authentication) throws Exception {
        Credential credential = googleCredentialService.getGoogleCredential(authentication);
        return new PeopleService.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Abrenica Oauth2").build();
    }

    // Create contact
    public void createContact(String givenName, String familyName, String email, String phoneNumber,
                              OAuth2AuthenticationToken authentication) throws Exception {
        PeopleService peopleService = buildPeopleService(authentication);
        Person contactToCreate = new Person();
        contactToCreate.setNames(List.of(new Name().setGivenName(givenName).setFamilyName(familyName)));

        if (email != null && !email.isEmpty()) {
            contactToCreate.setEmailAddresses(List.of(new EmailAddress().setValue(email)));
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            contactToCreate.setPhoneNumbers(List.of(new PhoneNumber().setValue(phoneNumber)));
        }
        peopleService.people().createContact(contactToCreate).execute();
    }

    // Get contacts
    public List<Person> getContacts(OAuth2AuthenticationToken authentication) throws Exception {
        PeopleService peopleService = buildPeopleService(authentication);
        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
                .setPersonFields("names,emailAddresses,phoneNumbers")
                .execute();
        return response.getConnections();
    }

    // Get contact by resourceName
    public Person getContact(String resourceName, OAuth2AuthenticationToken authentication) throws Exception {
        PeopleService peopleService = buildPeopleService(authentication);
        return peopleService.people().get(resourceName)
                .setPersonFields("names,emailAddresses,phoneNumbers")
                .execute();
    }

    // Update contact
    public void updateContact(String resourceName, String givenName, String familyName, String email, String phone,
                              OAuth2AuthenticationToken authentication) throws Exception {
        PeopleService peopleService = buildPeopleService(authentication);

        Person contactToUpdate = peopleService.people().get(resourceName)
                .setPersonFields("names,emailAddresses,phoneNumbers")
                .execute();

        contactToUpdate.setNames(List.of(new Name().setGivenName(givenName).setFamilyName(familyName)));
        if (email != null) {
            contactToUpdate.setEmailAddresses(List.of(new EmailAddress().setValue(email)));
        } else {
            contactToUpdate.setEmailAddresses(null);
        }
        if (phone != null) {
            contactToUpdate.setPhoneNumbers(List.of(new PhoneNumber().setValue(phone)));
        } else {
            contactToUpdate.setPhoneNumbers(null);
        }

        peopleService.people().updateContact(resourceName, contactToUpdate)
                .setUpdatePersonFields("names,emailAddresses,phoneNumbers")
                .execute();
    }

    // Delete contact
    public void deleteContact(String resourceName, OAuth2AuthenticationToken authentication) throws Exception {
        PeopleService peopleService = buildPeopleService(authentication);
        peopleService.people().deleteContact(resourceName).execute();
    }
}
