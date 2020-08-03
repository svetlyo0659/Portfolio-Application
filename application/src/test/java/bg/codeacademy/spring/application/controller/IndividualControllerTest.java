package bg.codeacademy.spring.application.controller;

import bg.codeacademy.spring.application.model.Individual;
import bg.codeacademy.spring.application.service.IndividualServiceImpl;
import bg.codeacademy.spring.application.service.PortfolioServiceImpl;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IndividualControllerTest extends AbstractTestNGSpringContextTests
{

  @LocalServerPort
  int port;

  @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextPrepareTestInstance")
  protected void setupRestAssured()
  {
    RestAssured.port = port;
  }

  @Autowired
  private IndividualServiceImpl individualService;
  private PortfolioServiceImpl  portfolioService;

  @Test
  public void createIndividual_200()
  {
    RestAssured
        .given()
        .param("name", "Test Individual")
        .param("type", "Private Entity")
        .param("identifier", "0000000000")
        .when()
        .post("/api/v1/individual")
        .then()
        .assertThat()
        .statusCode(200);

    Individual toDelete = individualService.findIndividualByName("Test Individual","0000000000");
    individualService.deleteIndividual(toDelete.getIdentifier(),toDelete.getName());

  }


  @Test
  public void createIndividual_400()
  {

    Individual individual = new Individual()
        .setName("Test Individual")
        .setType("Private Entity")
        .setIdentifier("0000000000");
    individualService.createIndividual(individual.getName(),individual.getType(),individual.getIdentifier());

    RestAssured
        .given()
        .param("name", "Test Individual")
        .param("type", "Private Entity")
        .param("identifier", "0000000000")
        .when()
        .post("/api/v1/individual")
        .then()
        .assertThat()
        .statusCode(400);

    Individual toDelete = individualService.findIndividualByName("Test Individual","0000000000");
    individualService.deleteIndividual(toDelete.getIdentifier(),toDelete.getName());

  }

  @Test
  public  void getAllIndividuals_200(){

    Individual individual1 = new Individual()
        .setName("Test Individual One")
        .setType("Private Entity")
        .setIdentifier("0000000001");
    individualService.createIndividual(individual1.getName(),individual1.getType(),individual1.getIdentifier());

    Individual individual2 = new Individual()
        .setName("Test Individual Two")
        .setType("Private Entity")
        .setIdentifier("0000000002");
    individualService.createIndividual(individual2.getName(),individual2.getType(),individual2.getIdentifier());

    RestAssured
        .given()
        .param("name", "")
        .param("type", "")
        .param("order", "name")
        .param("pageNum", "1")
        .param("rowsNum", "10")
        .when()
        .get("/api/v1/individual")
        .then()
        .assertThat()
        .statusCode(200);

    Individual toDelete = individualService.findIndividualByName("Test Individual One","0000000001");
    individualService.deleteIndividual(toDelete.getIdentifier(),toDelete.getName());

    Individual toDelete2 = individualService.findIndividualByName("Test Individual Two","0000000002");
    individualService.deleteIndividual(toDelete2.getIdentifier(),toDelete2.getName());
  }

  @Test
  public  void getAllIndividuals_400(){


    RestAssured
        .given()
        .param("name", "Individual One")
        .param("type", "")
        .param("order", "name")
        .param("pageNum", "1")
        .param("rowsNum", "10")
        .when()
        .get("/api/v1/individual")
        .then()
        .assertThat()
        .statusCode(400);
  }

  @Test
  public void deleteIndividual_200(){

    Individual individual1 = new Individual()
        .setName("Test Individual One")
        .setType("Private Entity")
        .setIdentifier("0000000001");
    individualService.createIndividual(individual1.getName(),individual1.getType(),individual1.getIdentifier());

    RestAssured
        .given()
        .param("identifier", "0000000001")
        .param("name", "Test Individual One")
        .when()
        .delete("/api/v1/individual")
        .then()
        .assertThat()
        .statusCode(200);

  }


}
