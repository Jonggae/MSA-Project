package com.jonggae.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET_KEY=${JWT_SECRET_KEY}",
        "JWT_REFRESH_SECRET_KEY=${JWT_REFRESH_SECRET_KEY}"
})
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }

}
