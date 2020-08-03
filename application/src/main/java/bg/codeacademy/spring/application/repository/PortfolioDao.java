package bg.codeacademy.spring.application.repository;

import bg.codeacademy.spring.application.model.Portfolio;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface PortfolioDao
{
  void createPortfolio(String docType, String credited, String debited, BigDecimal amount);

  void updatePortfolio(BigDecimal amnt, Timestamp date);

  List<Portfolio> getAllPortfolios(String name, String lei, String docType,
                                   String order, Integer pageNum, Integer rowsNum);

  BigDecimal getCurrentBalance(String indivId, Timestamp timestamp);

  BigDecimal getBalanceFromDtaeToDate(String indivId, Timestamp fromDate, Timestamp toDate);

  BigDecimal getTotal(String indivId, Timestamp fromDate, Timestamp toDate);

  String getIndividualName(String indivId, Timestamp timestamp);
}
