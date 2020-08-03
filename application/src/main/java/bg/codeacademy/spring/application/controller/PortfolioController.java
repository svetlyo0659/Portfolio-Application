package bg.codeacademy.spring.application.controller;

import bg.codeacademy.spring.application.model.Individual;
import bg.codeacademy.spring.application.model.Portfolio;
import bg.codeacademy.spring.application.service.IndividualServiceImpl;
import bg.codeacademy.spring.application.service.PortfolioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController
{
  private final PortfolioServiceImpl  portfolioService;
  private final IndividualServiceImpl individualService;
  private final Timestamp             timestamp = new Timestamp(System.currentTimeMillis());


  @Autowired
  public PortfolioController(PortfolioServiceImpl portfolioService, IndividualServiceImpl individualService)
  {
    this.portfolioService = portfolioService;
    this.individualService = individualService;
  }


  @PostMapping
  public ResponseEntity<?> addPortfolio(@RequestParam(name = "doc_type") String docType,
                                        @RequestParam(name = "first_individual") String firstIndivName,
                                        @RequestParam(name = "first_individual_LEI") String firstLei,
                                        @RequestParam(name = "second_individual") String secondIndivName,
                                        @RequestParam(name = "second_individual_LEI") String secondLei,
                                        @RequestParam(name = "amount") BigDecimal amount)
  {

    Individual firstIndividual = individualService.findIndividualByName(firstIndivName, firstLei);
    Individual secondIndividual = individualService.findIndividualByName(secondIndivName, secondLei);

    BigDecimal firstIndivBalance = portfolioService.getCurrentBalance(firstIndividual.getId(), timestamp);
    BigDecimal secondIndivBalance = portfolioService.getCurrentBalance(secondIndividual.getId(), timestamp);

    if (docType.equals("CREDIT NOTE")) {
      if (amount.intValue() <= 0) {
        return ResponseEntity.badRequest().body("Minimum amount 0.1 .");
      }
      if (firstIndivBalance.intValue() < amount.intValue()) {
        return ResponseEntity.badRequest().body("Not enough money.");
      }
      ///                               CREDIT NOTE , credited , debited , amount
      portfolioService.createPortfolio(docType, secondIndividual.getId(), firstIndividual.getId(), amount);
      return ResponseEntity.ok("Portfolio added");
    }
    else {
      if (amount.intValue() <= 0) {
        return ResponseEntity.badRequest().body("Minimum amount 0.1 .");
      }
      if (secondIndivBalance.intValue() < amount.intValue()) {
        return ResponseEntity.badRequest().body("Not enough money.");
      }
      ///                               INVOICE , credited , debited , amount
      portfolioService.createPortfolio(docType, firstIndividual.getId(), secondIndividual.getId(), amount);
      return ResponseEntity.ok("Portfolio added");
    }

  }


  @GetMapping("/balance")
  public ResponseEntity<?> getCurrentBalance(@RequestParam(name = "name") String name,
                                             @RequestParam(name = "lei") String lei,
                                             @RequestParam(name = "from_date", required = false)
                                                 String fromDate,
                                             @RequestParam(name = "to_date", required = false)
                                                 String toDate)
  {

    Individual individual = individualService.findIndividualByName(name, lei);
    if (individual != null) {

      Timestamp from = Timestamp.valueOf(fromDate);
      Timestamp to = Timestamp.valueOf(toDate);

      BigDecimal balance = portfolioService.getCurrentBalance(individual.getId(), timestamp);
      BigDecimal balanceFromTo = portfolioService.getBalanceFromDtaeToDate(individual.getId(), from, to);
      BigDecimal total = portfolioService.getTotalByIndividualId(individual.getId(), from, to);

      return ResponseEntity.ok("Current balance: " + balance + "\n" +
          "The balance in this time frame: " + balanceFromTo + "\n" +
          "Total for the period: " + total);
    }

    return ResponseEntity.badRequest().body("Individual not found");

  }

  @GetMapping("/getAll")
  public ResponseEntity<?> getAllPortfolios(@RequestParam(name = "doc_type",
      required = false, defaultValue = "") String docType,
                                            @RequestParam(name = "name",
                                                required = false, defaultValue = "") String name,
                                            @RequestParam(name = "lei",
                                                required = false, defaultValue = "") String lei,
                                            @RequestParam(name = "order",
                                                required = false, defaultValue = " '' ") String order,
                                            @RequestParam(name = "pageNum",
                                                required = false, defaultValue = "1") Integer pageNum,
                                            @RequestParam(name = "rowsNum",
                                                required = false, defaultValue = "10") Integer rowsNum)
  {

    List<Portfolio> all = portfolioService.getAllSortedPortfolios(name, lei, docType, order, pageNum, rowsNum);
    if (!all.isEmpty()) {

      return ResponseEntity.ok(all);
    }
    return ResponseEntity.badRequest().body("No portfolios found");

  }

  @PatchMapping("/update")
  public ResponseEntity<?> updateAmountInPortfolio(@RequestParam(name = "amount") BigDecimal amnt,
                                                   @RequestParam(name = "st_dt") String date)
  {
    Timestamp date1 = Timestamp.valueOf(date);
    portfolioService.updatePortfolio(amnt, date1);
    return ResponseEntity.ok("Amount updated");
  }

}
