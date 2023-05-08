package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private Keycloak keycloakClient;

    @Value("${keycloak.realm}")
    private String realm;


    @Test
    public void createTestUser() {
        UserRequest userRequest = new UserRequest("testuser", "testuser@example.com", "password", "Test", "User");
        if (keycloakClient.realm(realm).users().list().stream().noneMatch(p -> p.getEmail().equals("testuser@example.com"))) {
            userService.createUser(userRequest);
        }
        int count = keycloakClient.realm(realm).users().count();
        Assertions.assertEquals(userRequest.getEmail(), (keycloakClient.realm(realm).users().list().get(count - 1)).getEmail());
    }

    @Test
    public void getUserByID() {
        List<UserRepresentation> userRepresentationList = keycloakClient.realm(realm).users().list();
        UserRepresentation userRepresentation = userRepresentationList.get(userRepresentationList.size()-1);
        UserResponse userResponse = userService.getUserById(UUID.fromString(userRepresentation.getId()));
        Assertions.assertEquals(userRepresentation.getEmail(), userResponse.getEmail());
    }
}
