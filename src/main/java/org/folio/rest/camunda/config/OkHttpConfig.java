package org.folio.rest.camunda.config;

import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpConfig {

  /**
   * Add feignOkHttpClient to prevent issues introduced when using folio-spring-base.
   *
   * The issue involves enrichUrlAndHeadersClient, FeignClientConfiguration, and okhttp3.OkHttpClient.
   *
   * This defines a bean of type 'okhttp3.OkHttpClient' to prevent the error.
   */
  @Bean
  public OkHttpClient feignOkHttpClient() {
    return new OkHttpClient();
  }

}
