package bg.codeacademy.spring.application.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
public class SwaggerConfig implements WebMvcConfigurer
{

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry)
  {
    registry
        // serve the swagger-ui from the swagger-ui dependency
        .addResourceHandler("/api/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/3.25.0/")
        .resourceChain(false)

        // transform the index.html - to load the correct URL, instead of petstore
        .addTransformer((request, resource, transformerChain) -> {
          final AntPathMatcher antPathMatcher = new AntPathMatcher();
          boolean isIndexFound = antPathMatcher.match("**/swagger-ui/**/index.html", resource.getURL().toString());
          if (isIndexFound) {
            try (InputStream is = resource.getInputStream()) {
              String html = StreamUtils.copyToString(is, StandardCharsets.UTF_8)
                  .replace("https://petstore.swagger.io/v2/swagger.json", "/api.yml");
              return new TransformedResource(resource, html.getBytes());
            }
          }
          else {
            return resource;
          }
        })
    ;
  }
}
