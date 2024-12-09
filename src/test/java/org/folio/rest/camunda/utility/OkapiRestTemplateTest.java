package org.folio.rest.camunda.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.DefaultUriBuilderFactory;

@ExtendWith(MockitoExtension.class)
public class OkapiRestTemplateTest {

  @Test
  void testBuild() {
    OkapiRestTemplate restTemplate = OkapiRestTemplate.build();
    assertNotNull(restTemplate);
  }

  @Test
  void testAt() {
    OkapiRestTemplate restTemplate = spy(OkapiRestTemplate.class);

    String okapiUrl = "http:://localhost:9130";

    restTemplate = restTemplate.at(okapiUrl);

    verify(restTemplate, times(1))
      .setUriTemplateHandler(any(DefaultUriBuilderFactory.class));

    assertTrue(((DefaultUriBuilderFactory) restTemplate.getUriTemplateHandler()).hasBaseUri());
  }

  @Test
  void testWith() throws IOException {
    OkapiRestTemplate restTemplate = spy(OkapiRestTemplate.class);

    String tenant = "diku";
    String token = "token";

    restTemplate = restTemplate.with(tenant, token);

    verify(restTemplate, times(1))
      .setInterceptors(any(List.class));

    List<ClientHttpRequestInterceptor> interceptor = restTemplate.getInterceptors();

    assertNotNull(interceptor);
    assertEquals(1, interceptor.size());

    HttpRequest request = mock(HttpRequest.class);
    HttpHeaders headers = mock(HttpHeaders.class);
    ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
    ClientHttpResponse response = mock(ClientHttpResponse.class);

    byte[] body = new byte[0];

    when(execution.execute(request, body)).thenReturn(response);

    when(request.getHeaders()).thenReturn(headers);

    interceptor.get(0).intercept(request, body, execution);

    verify(headers).set("X-Okapi-Tenant", tenant);
    verify(headers).set("X-Okapi-Token", token);

    verify(headers).setAccept(Arrays.asList(APPLICATION_JSON, TEXT_PLAIN));
    verify(headers).setContentType(APPLICATION_JSON);
  }

}
