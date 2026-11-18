package ru.yandex.practicum.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Base64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({GatewaySecurityConfig.class})
class GatewaySecurityConfigTest {

    static final String IVAN_LOGIN = "ivan";
    static final String IVAN_PASSWORD = "ivan";

    static final String ANNA_LOGIN = "anna";
    static final String ANNA_PASSWORD = "anna";

    static final String DIMA_LOGIN = "dima";
    static final String DIMA_PASSWORD = "dima";

    static final String IVAN_WRONG_PASSWORD = "dime";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void anonymousGetProductsShouldNotBeBlockedBySecurity() {
        webTestClient.get()
            .uri("/api/products")
            .exchange()
            .expectStatus().is4xxClientError();
    }

    @Test
    void unauthenticatedPostOrderShouldReturn401() {
        webTestClient.post()
            .uri("/api/orders")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void ivanPostOrderShouldPassSecurityCheck() {
        webTestClient.post()
            .uri("/api/orders")
            .header("Authorization", basicAuth(IVAN_LOGIN, IVAN_PASSWORD))
            .exchange()
            .expectStatus().is4xxClientError();
    }

    @Test
    void ivanPutProductShouldReturn403() {
        webTestClient.put()
            .uri("/api/products/1")
            .header("Authorization", basicAuth(IVAN_LOGIN, IVAN_PASSWORD))
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void annaPutProductShouldPassSecurityCheck() {
        webTestClient.put()
            .uri("/api/products/1")
            .header("Authorization", basicAuth(ANNA_LOGIN, ANNA_PASSWORD))
            .exchange()
            .expectStatus().is4xxClientError();
    }

    @Test
    void ivanGetOrdersShouldReturn403() {
        webTestClient.get()
            .uri("/api/orders")
            .header("Authorization", basicAuth(IVAN_LOGIN, IVAN_PASSWORD))
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void annaGetOrdersShouldPassSecurityCheck() {
        webTestClient.get()
            .uri("/api/orders")
            .header("Authorization", basicAuth(ANNA_LOGIN, ANNA_PASSWORD))
            .exchange()
            .expectStatus().is4xxClientError();
    }

    @Test
    void annaGetUnknownRouteShouldReturn403() {
        webTestClient.get()
            .uri("/api/unknown")
            .header("Authorization", basicAuth(ANNA_LOGIN, ANNA_PASSWORD))
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void unknownUserShouldReturn401() {
        webTestClient.get()
            .uri("/api/products")
            .header("Authorization", basicAuth(DIMA_LOGIN, DIMA_PASSWORD))
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void ivanWithWrongPasswordShouldReturn401() {
        webTestClient.get()
            .uri("/api/products")
            .header("Authorization", basicAuth(IVAN_LOGIN, IVAN_WRONG_PASSWORD))
            .exchange()
            .expectStatus().isUnauthorized();
    }

    private String basicAuth(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        return "Basic " + new String(encodedAuth);
    }
}
