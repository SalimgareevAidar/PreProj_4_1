package com.itm.space.backendresources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerIntegrationTest extends BaseIntegrationTest {

    private String token = "Bearer " + getTokenFromKeycloak("Faren", "123");

    public UserControllerIntegrationTest() throws Exception {
    }

    private String getTokenFromKeycloak(String username, String password) throws Exception {
        String clientId = "backend-gateway-client";
        String clientSecret = "OKsr85IpK4zF4bqf4g5mjunq6HYGjWh9";
        String grantType = "password";
        String scope = "openid";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", grantType);
        map.add("username", username);
        map.add("password", password);
        map.add("scope", scope);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://backend-keycloak-auth:8080/auth/realms/ITM/protocol/openid-connect/token", request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.getBody());
        return node.get("access_token").asText();
    }


    @Test
    public void creatingNewUserByModerator() throws Exception {
        UserRequest testNewUser = new UserRequest("Faren2", "fapeh2@yandex.ru", "1234", "Aidar", "Salimgareev");
        this.mvc.perform(requestWithContent(post("/api/users").header("Authorization", token), testNewUser)).andExpect(status().isOk());
    }

    @Test
    public void creatingNewUserExpectingValidError() throws Exception {
        UserRequest testNotValidNewUser = new UserRequest("Faren2", "faren", "1234", "Aidar", "Salimgareev");
        this.mvc.perform(requestWithContent(post("/api/users").header("Authorization", token),testNotValidNewUser)).andExpect(status().is(400)).andExpect(jsonPath("$.email").value("Email should be valid"));
    }


    @Test
    public void gettingHelloMessage() throws Exception {
        this.mvc.perform(get("/api/users/hello").header("Authorization", token)).andExpect(status().isOk());
    }


    @Test
    public void gettingUserWithIDByModerator() throws Exception {
        String testGUID = "80261891-f5e2-45ff-9540-1b82ade2fdb6";
        this.mvc.perform(get("/api/users/" + testGUID).header("Authorization", token)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("fapeh@yandex.ru"));
    }
}
