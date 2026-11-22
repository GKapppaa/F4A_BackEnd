package F4A.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.jwt.secret=clave-de-prueba-de-al-menos-32-caracteres")
class WebApplicationTests {

	@Test
	void contextLoads() {
	}

}
