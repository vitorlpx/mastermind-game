package com.br.mastermind.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"jwt.secret=test-secret-key-for-testing-purposes-only-32chars",
	"jwt.expiration=3600000"
})
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
