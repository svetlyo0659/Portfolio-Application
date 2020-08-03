package bg.codeacademy.spring.application.repository;

import bg.codeacademy.spring.application.model.Portfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class PortfolioRepository implements PortfolioDao
{

  private DataSource                 dataSource;
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  @Autowired
  public PortfolioRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             DataSource dataSource)
  {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.dataSource = dataSource;
  }

  @Override
  public void createPortfolio(String docType, String credited, String debited, BigDecimal amount)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("doc_type", docType);
    mapSqlParameterSource.addValue("amnt", amount);
    mapSqlParameterSource.addValue("cr", credited);
    mapSqlParameterSource.addValue("db", debited);

    final String sql = "INSERT INTO PORTFOLIO (doc_type, amnt, cr, db) VALUES(:doc_type, :amnt, :cr, :db) ";
    namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
  }

  @Override
  public void updatePortfolio(BigDecimal amnt, Timestamp date)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("amnt", amnt);
    mapSqlParameterSource.addValue("st_dt", date);

    final String sql =
        "UPDATE PORTFOLIO " +
            "SET amnt = :amnt " +
            "WHERE st_id = (SELECT st_id FROM " +
            "TBL_STATEMENT WHERE " +
            "st_dt = TO_TIMESTAMP(TO_CHAR(:st_dt),'YYYY-MM-DD HH24:MI:SS.FF') + 3/24 ) ";

    namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
  }


  @Override
  public List<Portfolio> getAllPortfolios(String name, String lei,
                                          String docType, String order,
                                          Integer pageNum, Integer rowsNum)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("name", name + "%");
    mapSqlParameterSource.addValue("lei", lei + "%");
    mapSqlParameterSource.addValue("doc_type", docType + "%");
    mapSqlParameterSource.addValue("page_num", pageNum);
    mapSqlParameterSource.addValue("rows_num", rowsNum);

    final String sql =
        "SELECT * " +
            "FROM (SELECT foo.* , ROWNUM AS page_size " +
            "FROM  (SELECT doc_type, amnt, name, cr, db, idiv_id, type, lei,st_dt FROM PORTFOLIO p " +
            "INNER JOIN INDIVIDUALS i " +
            "ON p.cr = i.idiv_id " +
            "INNER JOIN TBL_STATEMENT s " +
            "ON p.st_id = s.st_id " +
            "WHERE doc_type LIKE :doc_type " +
            "AND name LIKE  :name " +
            "AND LEI LIKE :lei " +
            "ORDER BY " + order + " )foo " +
            "WHERE  ROWNUM <= :rows_num *(:page_num)) " +
            "WHERE page_size > ( :page_num - 1) * :rows_num ";
    try {

      return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource,
          (rs, rowNum) ->
              new Portfolio()
                  .setDocType(rs.getString("doc_type"))
                  .setAmount(rs.getBigDecimal("amnt"))
                  .setCredited(getIndividualName(
                      rs.getString("cr"),
                      rs.getTimestamp("st_dt")))
                  .setDebited(getIndividualName(
                      rs.getString("db"),
                      rs.getTimestamp("st_dt")))
                  .setLei(rs.getString("lei"))
                  .setType(rs.getString("type"))
                  .setDate(rs.getTimestamp("st_dt"))
                  .setRunningBalance(getCurrentBalance(
                      rs.getString("cr"),
                      rs.getTimestamp("st_dt")))
                  .setRunningBalanceDb(getCurrentBalance(
                      rs.getString("db"),
                      rs.getTimestamp("st_dt")))
      );
    }
    catch (EmptyResultDataAccessException ex) {
      return null;
    }

  }

  @Override
  public BigDecimal getCurrentBalance(String indivId, Timestamp timestamp)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("indiv_id", indivId);
    mapSqlParameterSource.addValue("in_time", timestamp);

    SimpleJdbcCall function = new SimpleJdbcCall(dataSource)
        .withFunctionName("get_balance");

    return function.executeFunction(BigDecimal.class, mapSqlParameterSource);
  }

  @Override
  public BigDecimal getBalanceFromDtaeToDate(String indivId, Timestamp fromDate, Timestamp toDate)
  {

    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("idiv_id", indivId);
    mapSqlParameterSource.addValue("from_dt", fromDate);
    mapSqlParameterSource.addValue("to_dt", toDate);


    final String sql = "SELECT " +
        "((SELECT NVL(SUM(amnt),0) " +
        "FROM PORTFOLIO p " +
        "INNER JOIN TBL_STATEMENT s " +
        "ON s.st_id = p.st_id " +
        "WHERE cr = :idiv_id " +
        "AND st_dt " +
        "BETWEEN TO_TIMESTAMP( TO_CHAR(:from_dt) , 'YYYY-MM-DD HH24:MI:SS.FF') " +
        "AND TO_TIMESTAMP( TO_CHAR(:to_dt) , 'YYYY-MM-DD HH24:MI:SS.FF')) - " +
        "(SELECT NVL(SUM(amnt),0) FROM PORTFOLIO p " +
        "INNER JOIN TBL_STATEMENT s " +
        "ON s.st_id = p.st_id " +
        "WHERE db = :idiv_id AND st_dt " +
        "BETWEEN TO_TIMESTAMP( TO_CHAR(:from_dt) , 'YYYY-MM-DD HH24:MI:SS.FF') " +
        "AND TO_TIMESTAMP( TO_CHAR(:to_dt) , 'YYYY-MM-DD HH24:MI:SS.FF')))" +
        "FROM DUAL ";

    try {
      return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, BigDecimal.class);
    }
    catch (NullPointerException ex) {
      return BigDecimal.ZERO;
    }
  }

  @Override
  public BigDecimal getTotal(String indivId, Timestamp fromDate, Timestamp toDate)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("idiv_id", indivId);
    mapSqlParameterSource.addValue("from_dt", fromDate);
    mapSqlParameterSource.addValue("to_dt", toDate);

    final String sql = "SELECT " +
        "(SELECT NVL(SUM(amnt),0) " +
        "FROM PORTFOLIO p " +
        "INNER JOIN TBL_STATEMENT s " +
        "ON s.st_id = p.st_id " +
        "WHERE cr = :idiv_id AND st_dt " +
        "BETWEEN TO_TIMESTAMP( TO_CHAR(:from_dt) , 'YYYY-MM-DD HH24:MI:SS.FF') " +
        "AND TO_TIMESTAMP( TO_CHAR(:to_dt) , 'YYYY-MM-DD HH24:MI:SS.FF')) " +
        "FROM DUAL ";
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, BigDecimal.class);
    }
    catch (NullPointerException ex) {
      return BigDecimal.ZERO;
    }

  }

  @Override
  public String getIndividualName(String individualId, Timestamp date)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("individual_id", individualId);
    mapSqlParameterSource.addValue("in_time", date);

    SimpleJdbcCall function = new SimpleJdbcCall(dataSource)
        .withFunctionName("get_name");
    return function.executeFunction(String.class, mapSqlParameterSource);
  }
}
