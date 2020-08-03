package bg.codeacademy.spring.application.service;

import bg.codeacademy.spring.application.model.Portfolio;
import bg.codeacademy.spring.application.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class PortfolioServiceImpl implements PortfolioService
{

  @Autowired
  private PortfolioRepository   portfolioRepository;
  private IndividualServiceImpl individualService;


  @Override
  public void createPortfolio(String docType, String cr, String db, BigDecimal amount)
  {

    portfolioRepository.createPortfolio(docType, cr, db, amount);
  }

  @Override
  public void updatePortfolio(BigDecimal amnt, Timestamp date)
  {
    portfolioRepository.updatePortfolio(amnt, date);
  }

  @Override
  public BigDecimal getCurrentBalance(String indivId, Timestamp timestamp)
  {
    return portfolioRepository.getCurrentBalance(indivId, timestamp);
  }

  @Override
  public BigDecimal getBalanceFromDtaeToDate(String indivId, Timestamp fromDate, Timestamp toDate)
  {
    return portfolioRepository.getBalanceFromDtaeToDate(indivId, fromDate, toDate);
  }

  @Override
  public BigDecimal getTotalByIndividualId(String indivId, Timestamp fromDate, Timestamp toDate)
  {
    return portfolioRepository.getTotal(indivId, fromDate, toDate);
  }

  @Override
  public List<Portfolio> getAllSortedPortfolios(String name, String lei,
                                                String docType, String order,
                                                Integer pageNum, Integer rowsNum)
  {
    return portfolioRepository.getAllPortfolios(name, lei, docType, order, pageNum, rowsNum);
  }
}
