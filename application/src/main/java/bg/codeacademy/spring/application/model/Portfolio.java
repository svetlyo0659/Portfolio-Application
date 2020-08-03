package bg.codeacademy.spring.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import oracle.sql.TIMESTAMP;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class Portfolio
{

  @JsonIgnore
  private String id;

  private String docType;

  private BigDecimal amount;

  private String credited;

  private String type;

  private String lei;

  private BigDecimal runningBalance;

  private String debited;

  private BigDecimal runningBalanceDb;



  private Timestamp date;

  public String getCredited()
  {
    return credited;
  }

  public Portfolio setCredited(String credited)
  {
    this.credited = credited;
    return this;
  }

  public String getDebited()
  {
    return debited;
  }

  public Portfolio setDebited(String debited)
  {
    this.debited = debited;
    return this;
  }

  @JsonIgnore
  public String getId()
  {
    return id;
  }

  public Portfolio setId(String id)
  {
    this.id = id;
    return this;
  }

  public String getDocType()
  {
    return docType;
  }

  public Portfolio setDocType(String docType)
  {
    this.docType = docType;
    return this;
  }


  public String getType()
  {
    return type;
  }

  public Portfolio setType(String type)
  {
    this.type = type;
    return this;
  }

  public String getLei()
  {
    return lei;
  }

  public Portfolio setLei(String lei)
  {
    this.lei = lei;
    return this;
  }

  public BigDecimal getAmount()
  {
    return amount;
  }

  public Portfolio setAmount(BigDecimal amount)
  {
    this.amount = amount;
    return this;
  }

  public BigDecimal getRunningBalance()
  {
    return runningBalance;
  }

  public Portfolio setRunningBalance(BigDecimal runningBalance)
  {
    this.runningBalance = runningBalance;
    return this;
  }

  public Timestamp getDate()
  {
    return date;
  }

  public Portfolio setDate(Timestamp date)
  {
    this.date = date;
    return this;
  }


  public BigDecimal getRunningBalanceDb()
  {
    return runningBalanceDb;
  }

  public Portfolio setRunningBalanceDb(BigDecimal runningBalanceDb)
  {
    this.runningBalanceDb = runningBalanceDb;
    return this;
  }
}
