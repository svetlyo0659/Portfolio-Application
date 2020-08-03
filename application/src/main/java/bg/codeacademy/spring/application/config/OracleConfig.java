package bg.codeacademy.spring.application.config;


import oracle.jdbc.pool.OracleDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@ConfigurationProperties("oracle")
public class OracleConfig
{

  private String username;

  private String password;

  private String url;

  public void setUsername(String username) {
    this.username = username;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public void setUrl(String url) {
    this.url = url;
  }


  @Bean
  DataSource dataSource() throws SQLException {
    OracleDataSource dataSource = new OracleDataSource();
    dataSource.setUser(username);
    dataSource.setPassword(password);
    dataSource.setURL(url);
    dataSource.setImplicitCachingEnabled(true);
    dataSource.setFastConnectionFailoverEnabled(true);
    return dataSource;
  }
}
