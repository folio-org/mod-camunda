package org.folio.rest.camunda.utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

public class OkapiRestTemplate extends RestTemplate {

  private static final String OKAPI_TENENT_HEADER = "X-Okapi-Tenant";
  private static final String OKAPI_TOKEN_HEADER = "X-Okapi-Token";

  private OkapiRestTemplate() {

  }

  public OkapiRestTemplate with(String tenant, String token) {
    this.setInterceptors(Collections.singletonList(new ClientHttpRequestInterceptor() {
      @Override
      public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
          throws IOException {
        HttpHeaders headers = request.getHeaders();

        headers.set(OKAPI_TENENT_HEADER, token);
        headers.set(OKAPI_TOKEN_HEADER, tenant);

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return execution.execute(request, body);
      }
    }));

    return this;
  }

  public OkapiRestTemplate at(String okapiUrl) {
    DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(okapiUrl);
    this.setUriTemplateHandler(factory);

    return this;
  }

  public static OkapiRestTemplate build() {
    OkapiRestTemplate restTemplate = new OkapiRestTemplate();

    return restTemplate;
  }

}
