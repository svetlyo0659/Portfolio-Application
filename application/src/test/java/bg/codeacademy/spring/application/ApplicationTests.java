package bg.codeacademy.spring.application;

import io.restassured.RestAssured;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests extends AbstractTestNGSpringContextTests
{
	@LocalServerPort
	int port;

	@BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextPrepareTestInstance")
	protected void setupRestAssured()
	{
		RestAssured.port = port;
	}

	@Test
	void contextLoads()
	{

	}


}
