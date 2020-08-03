package bg.codeacademy.spring.application.repository;

import bg.codeacademy.spring.application.model.Individual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Repository
@Transactional
public class IndividualRepository implements IndividualDao
{

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private DataSource                 dataSource;

  @Autowired
  public IndividualRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              DataSource dataSource)
  {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.dataSource = dataSource;
  }


  @Override
  public void createIndividual(Individual individual)
  {

    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("name", individual.getName());
    mapSqlParameterSource.addValue("type", individual.getType());
    mapSqlParameterSource.addValue("lei", individual.getIdentifier());

    final String sql = "INSERT INTO INDIVIDUALS(name, type, lei) VALUES(:name, :type, :lei)";
    namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
  }

  @Override
  public void deleteIndividual(Individual individual)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("lei", individual.getIdentifier());
    mapSqlParameterSource.addValue("name", individual.getName());

    final String sql =
        "DELETE FROM INDIVIDUALS WHERE lei = :lei AND name = :name ";

    namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
  }

  @Override
  public List<Individual> getAllIndividuals(String name, String type,
                                            String order, Integer pageNum,
                                            Integer rowsNum)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("name",  name + "%");
    mapSqlParameterSource.addValue("type",  type + "%");
    mapSqlParameterSource.addValue("rows_num", rowsNum);
    mapSqlParameterSource.addValue("page_num", pageNum);

    final String sql =
        "SELECT idiv_id, name, type, lei " +
            "FROM INDIVIDUALS " +
            "WHERE NAME LIKE :name " +
            "AND TYPE LIKE :type " +
            "ORDER BY " + order + " " +
            "OFFSET ( :page_num - 1)* :rows_num ROWS " +
            "FETCH NEXT :rows_num ROWS ONLY ";
    try {
      return namedParameterJdbcTemplate.query(
          sql, mapSqlParameterSource,
          (rs, rowNum) ->
              new Individual()
                  .setId(rs.getString("idiv_id"))
                  .setName(rs.getString("name"))
                  .setType(rs.getString("type"))
                  .setIdentifier(rs.getString("lei"))
      );
    }
    catch (EmptyResultDataAccessException ex) {
      return null;
    }

  }

  @Override // DO NOT TOUCH !!! DOES NOT RETURN BOOLEAN TYPE
  public BigDecimal containsIndividual(String name)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("indiv_name", name);

    SimpleJdbcCall function = new SimpleJdbcCall(dataSource)
        .withFunctionName("indiv_exists");

    return function.executeFunction(BigDecimal.class, mapSqlParameterSource);

  }

  @Override
  public BigDecimal containsIdentifier(String identifier)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("identifier_num", identifier);

    SimpleJdbcCall function = new SimpleJdbcCall(dataSource)
        .withFunctionName("identifier_exist");

    return function.executeFunction(BigDecimal.class, mapSqlParameterSource);
  }

  @Override
  public Individual findIndividualByName(String name, String lei)
  {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("name", name);
    mapSqlParameterSource.addValue("lei", lei);

    final String sql =
        "SELECT idiv_id, name, type, lei " +
            "FROM INDIVIDUALS " +
            "WHERE name = :name " +
            "AND LEI = :lei ";

    try {
      return namedParameterJdbcTemplate.queryForObject(
          sql, mapSqlParameterSource,
          (rs, rowNum) ->
              new Individual()
                  .setId(rs.getString("idiv_id"))
                  .setName(rs.getString("name"))
                  .setType(rs.getString("type"))
                  .setIdentifier(rs.getString("lei"))
      );
    }
    catch (EmptyResultDataAccessException ex) {
      return null;
    }

  }

}
