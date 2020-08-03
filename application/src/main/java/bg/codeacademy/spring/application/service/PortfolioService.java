package bg.codeacademy.spring.application.service;

import bg.codeacademy.spring.application.model.Portfolio;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


public interface PortfolioService
{
  void createPortfolio(String docType, String cr, String db, BigDecimal amount);

  void updatePortfolio(BigDecimal amnt, Timestamp date);

  BigDecimal getCurrentBalance(String indivId, Timestamp timestamp);

  BigDecimal getBalanceFromDtaeToDate(String indivId, Timestamp fromDate, Timestamp toDate);

  BigDecimal getTotalByIndividualId(String indivId, Timestamp fromDate, Timestamp toDate);

  List<Portfolio> getAllSortedPortfolios(String name, String lei,
                                         String docType, String order,
                                         Integer pageNum, Integer rowsNum);
}
