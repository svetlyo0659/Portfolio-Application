package bg.codeacademy.spring.application.controller;


import bg.codeacademy.spring.application.model.Individual;
import bg.codeacademy.spring.application.model.Portfolio;
import bg.codeacademy.spring.application.repository.PortfolioRepository;
import bg.codeacademy.spring.application.service.IndividualServiceImpl;
import bg.codeacademy.spring.application.service.PortfolioServiceImpl;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PortfolioControllerTest extends AbstractTestNGSpringContextTests
{
  @LocalServerPort
  private int port;

  @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextPrepareTestInstance")
  protected void setupRestAssured()
  {
    RestAssured.port = port;
  }

  @Autowired
  private IndividualServiceImpl individualService;
  private PortfolioServiceImpl  portfolioService;
  private PortfolioRepository   portfolioRepository;


  //@BeforeClass
  //public void createIndividuals()
  //{
//
//
  //  /// adds an amount in the
//
  //}

  //@AfterClass
  //public void removeIndividuals()
  //{
//
  //  Individual toDelete = individualService.findIndividualByName("Test Individual One", "0000000001");
  //  individualService.deleteIndividual(toDelete.getIdentifier(), toDelete.getName());
//
  //  Individual toDelete2 = individualService.findIndividualByName("Test Individual Two", "0000000002");
  //  individualService.deleteIndividual(toDelete2.getIdentifier(), toDelete2.getName());
//
  //}


  @Test
  void createPortfolio()
  {
    Individual individual1 = new Individual()
        .setName("Test Individual One")
        .setType("Legal Entity")
        .setIdentifier("0000000001");
    individualService.createIndividual(individual1.getName(), individual1.getType(), individual1.getIdentifier());

    Individual individual2 = new Individual()
        .setName("Test Individual Two")
        .setType("Private Entity")
        .setIdentifier("0000000002");
    individualService.createIndividual(individual2.getName(), individual2.getType(), individual2.getIdentifier());

    Individual credited = individualService.findIndividualByName(individual1.getName(), individual1.getIdentifier());
    Individual debited = individualService.findIndividualByName(individual2.getName(), individual2.getIdentifier());

    //There is a trigger {AFTER INSERT} on table INDIVIDUALS that creates a PORTFOLIO for every
    // individual(credited) INSERTED  with the amount of 1000.

    RestAssured
        .given()
        .param("doc_type", "CREDIT NOTE")
        .param("first_individual", individual1.getName())
        .param("first_individual_LEI", individual1.getIdentifier())
        .param("second_individual", individual2.getName())
        .param("second_individual_LEI", individual2.getIdentifier())
        .param("amount", BigDecimal.ONE)
        .when()
        .post("/api/v1/portfolio")
        .then()
        .assertThat()
        .statusCode(200);

    Individual toDelete = individualService.findIndividualByName("Test Individual One", "0000000001");
    individualService.deleteIndividual(toDelete.getIdentifier(), toDelete.getName());

    Individual toDelete2 = individualService.findIndividualByName("Test Individual Two", "0000000002");
    individualService.deleteIndividual(toDelete2.getIdentifier(), toDelete2.getName());

  }


  @Test
  public void getBalance_200()
  {
    Individual individual1 = new Individual()
        .setName("Test Individual One")
        .setType("Legal Entity")
        .setIdentifier("0000000001");
    individualService.createIndividual(individual1.getName(), individual1.getType(), individual1.getIdentifier());

    Individual individual = individualService.findIndividualByName(individual1.getName(), individual1.getIdentifier());

    Timestamp fromDtae = new Timestamp(System.currentTimeMillis() - 10);
    Timestamp toDate = new Timestamp(System.currentTimeMillis());
    RestAssured
        .given()
        .param("name", individual.getName())
        .param("lei", individual.getIdentifier())
        .param("from_date", fromDtae.toString())
        .param("to_date", toDate.toString())
        .when()
        .get("/api/v1/portfolio/balance")
        .then()
        .assertThat()
        .statusCode(200);

    Individual toDelete = individualService.findIndividualByName("Test Individual One", "0000000001");
    individualService.deleteIndividual(toDelete.getIdentifier(), toDelete.getName());
  }


  @Test
  public void getAllPortfoliosTest_200()
  {
    Individual individual1 = new Individual()
        .setName("Test Individual One")
        .setType("Legal Entity")
        .setIdentifier("0000000001");
    individualService.createIndividual(individual1.getName(), individual1.getType(), individual1.getIdentifier());

    Individual individual2 = new Individual()
        .setName("Test Individual Two")
        .setType("Private Entity")
        .setIdentifier("0000000002");
    individualService.createIndividual(individual2.getName(), individual2.getType(), individual2.getIdentifier());

    Individual credited = individualService.findIndividualByName(individual1.getName(), individual1.getIdentifier());
    Individual debited = individualService.findIndividualByName(individual2.getName(), individual2.getIdentifier());


    RestAssured
        .given()
        .param("doc_type", "")
        .param("name", "")
        .param("lei", "")
        .param("order", "")
        .param("pageNum", "1")
        .param("rowsNum", "10")
        .when()
        .get("/api/v1/portfolio/getAll")
        .then()
        .assertThat()
        .statusCode(200);

    Individual toDelete = individualService.findIndividualByName("Test Individual One", "0000000001");
    individualService.deleteIndividual(toDelete.getIdentifier(), toDelete.getName());

    Individual toDelete2 = individualService.findIndividualByName("Test Individual Two", "0000000002");
    individualService.deleteIndividual(toDelete2.getIdentifier(), toDelete2.getName());
  }

  @Test
  public void getAllPortfoliosTest_400(){

    RestAssured
        .given()
        .param("doc_type", "")
        .param("name", "TESTING400")
        .param("lei", "")
        .param("order", "")
        .param("pageNum", "1")
        .param("rowsNum", "10")
        .when()
        .get("/api/v1/portfolio/getAll")
        .then()
        .assertThat()
        .statusCode(400);
  }


}
