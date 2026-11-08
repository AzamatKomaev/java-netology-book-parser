package ru.netology.prices;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "app.seed.enabled=false", "spring.datasource.url=jdbc:h2:mem:test" })
class PriceParserApplicationTests {

	@Test
	void contextLoads() {
	}

}
