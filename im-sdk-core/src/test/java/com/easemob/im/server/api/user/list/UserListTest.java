package com.easemob.im.server.api.user.list;

import com.easemob.im.server.api.AbstractApiTest;
import com.easemob.im.server.model.EMUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserListTest extends AbstractApiTest {

    public UserListTest() {
        this.server.addHandler("GET /easemob/demo/users?limit=100", this::handleUserGet100);
        this.server.addHandler("GET /easemob/demo/users?limit=100&cursor=cursor-0-99", this::handleUserGet100Continued);
        this.server.addHandler("GET /easemob/demo/users?limit=100&cursor=cursor-100-199", this::handleUserGet100Last);
        this.server.addHandler("GET /easemob/demo/users?limit=200", this::handleUserGet200);
        this.server.addHandler("GET /easemob/demo/users?limit=200&cursor=cursor-0-199", this::handleUserGet200Continued);
    }

    @Test
    public void testUserGetAll100EachTime() {
        List<EMUser> users = UserList.all(this.context, 100)
                .collectList().block(Duration.ofSeconds(3));
        assertEquals(300, users.size());
        for (int i = 0; i < 300; i++) {
            assertEquals("username", users.get(i).getUsername());
        }
    }

    @Test
    public void testUserGetAll200EachTime() {
        List<EMUser> users = UserList.all(this.context, 200)
                .collectList().block(Duration.ofSeconds(3));
        assertEquals(300, users.size());
        for (int i = 0; i < 300; i++) {
            assertEquals("username", users.get(i).getUsername());
        }
    }



    private JsonNode handleUserGet100(JsonNode req) {
        ObjectNode user = this.objectMapper.createObjectNode();
        user.put("username", "username");

        ArrayNode users = this.objectMapper.createArrayNode();
        for (int i = 0; i < 100; i++) {
            users.add(user);
        }

        ObjectNode rsp = this.objectMapper.createObjectNode();
        rsp.set("entities", users);
        rsp.put("cursor", "cursor-0-99");

        return rsp;
    }

    private JsonNode handleUserGet100Continued(JsonNode req) {
        ObjectNode user = this.objectMapper.createObjectNode();
        user.put("username", "username");

        ArrayNode users = this.objectMapper.createArrayNode();
        for (int i = 0; i < 100; i++) {
            users.add(user);
        }

        ObjectNode rsp = this.objectMapper.createObjectNode();
        rsp.set("entities", users);
        rsp.put("cursor", "cursor-100-199");

        return rsp;
    }

    private JsonNode handleUserGet100Last(JsonNode req) {
        ObjectNode user = this.objectMapper.createObjectNode();
        user.put("username", "username");

        ArrayNode users = this.objectMapper.createArrayNode();
        for (int i = 0; i < 100; i++) {
            users.add(user);
        }

        ObjectNode rsp = this.objectMapper.createObjectNode();
        rsp.set("entities", users);

        return rsp;
    }

    private JsonNode handleUserGet200(JsonNode req) {
        ObjectNode user = this.objectMapper.createObjectNode();
        user.put("username", "username");

        ArrayNode users = this.objectMapper.createArrayNode();
        for (int i = 0; i < 200; i++) {
            users.add(user);
        }

        ObjectNode rsp = this.objectMapper.createObjectNode();
        rsp.set("entities", users);
        rsp.put("cursor", "cursor-0-199");

        return rsp;
    }

    private JsonNode handleUserGet200Continued(JsonNode req) {
        ObjectNode user = this.objectMapper.createObjectNode();
        user.put("username", "username");

        ArrayNode users = this.objectMapper.createArrayNode();
        for (int i = 0; i < 100; i++) {
            users.add(user);
        }

        ObjectNode rsp = this.objectMapper.createObjectNode();
        rsp.set("entities", users);

        return rsp;
    }
}