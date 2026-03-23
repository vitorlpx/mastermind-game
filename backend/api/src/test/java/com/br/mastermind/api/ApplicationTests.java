package com.br.mastermind.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.br.mastermind.api.repository.MatchRepository;
import com.br.mastermind.api.repository.UserRepository;

@SpringBootTest
@TestPropertySource(properties = {
	"jwt.secret=test-secret-key-for-testing-purposes-only-32chars",
	"jwt.expiration=3600000",
	"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class ApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private MatchRepository matchRepository;

	@Test
	void contextLoads() {
	}

}
