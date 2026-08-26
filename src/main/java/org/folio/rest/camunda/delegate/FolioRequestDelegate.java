package org.folio.rest.camunda.delegate;

import static org.folio.rest.camunda.cache.FolioTokenCache.FOLIO_ACCESS_TOKEN;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import org.folio.rest.camunda.cache.FolioTokenCache;
import org.folio.rest.camunda.exception.DelegateExecutionFailure;
import org.folio.rest.workflow.dto.Request;
import org.folio.rest.workflow.model.FolioRequestTask;
import org.folio.spring.web.service.HttpService;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import tools.jackson.databind.json.JsonMapper;

/**
 * A delegate for performing already logged in FOLIO HTTP requests.
 *
 * For logging into FOLIO, use the RequestDelegate instead.
 */
@Service
@Scope("prototype")
public class FolioRequestDelegate extends RequestDelegate {

  private FolioTokenCache folioTokenCache;

  /**
   * Initializer.
   */
  public FolioRequestDelegate(JsonMapper mapper, RuntimeService runtimeService, HttpService httpService, FolioTokenCache folioTokenCache) {

    super(mapper, runtimeService, httpService);

    this.folioTokenCache = folioTokenCache;
  }

  @Override
  public Class<?> fromTask() {
    return FolioRequestTask.class;
  }

  /**
   * Perform the execution.
   *
   * @param execution The execution data.
   * @param name      The delegate name.
   * @param id        The delegate ID.
   */
  @Override
  protected void performExecute(DelegateExecution execution, String name, String id) {

    final Map<String, Object> inputs = getInputs(execution);
    final Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

    final Request requestValue = mapper.readValue(this.request.getValue(execution).toString(), Request.class);
    final Boolean sendEmptyBody = requestValue.getSendEmptyBody();

    final StringTemplateLoader loader = new StringTemplateLoader();
    cfg.setTemplateLoader(loader);

    final String body;
    final String url;

    try {
      body = performExecuteBuildBody(requestValue.getBodyTemplate(), loader, cfg, inputs);
      url = performExecuteBuildUrl(requestValue.getUrl(), loader, cfg, inputs);
    } catch (IOException | TemplateException e) {
      throw new DelegateExecutionFailure(name, id, e.getMessage(), e);
    }

    if (url == null) {
      throw new DelegateExecutionFailure(name, id, "No URL specified.");
    }

    final HttpMethod method = HttpMethod.valueOf(requestValue.getMethod().toString());
    final String accept = requestValue.getAccept();
    final String contentType = requestValue.getContentType();

    final String tenant = execution.getTenantId();

    getLogger().info("url: {}", url);
    getLogger().debug("method: {}", method);
    getLogger().debug("sendEmptyBody: {}", sendEmptyBody);

    getLogger().debug("accept: {}", accept);
    getLogger().debug("content-type: {}", contentType);
    getLogger().debug("tenant: {}", tenant);

    final HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", accept);
    headers.add("Content-Type", contentType);
    headers.add("X-Okapi-Tenant", tenant);
    headers.add("X-Okapi-Url", okapiUrl);

    final HttpEntity<Object> entity = shouldSendBody(body, sendEmptyBody, method)
      ? new HttpEntity<>(body, headers)
      : new HttpEntity<>(headers);

    final String accessToken = folioTokenCache.verifyTokens(execution);

    if (accessToken != null) {
      headers.add("Cookie", String.format("%s=%s", FOLIO_ACCESS_TOKEN, accessToken));
      headers.add("X-Okapi-Token", accessToken);
    }

    try {
      final ResponseEntity<Object> response = httpService.exchange(url, method, entity, Object.class);

      setOutput(execution, response.getBody());

      getHeaderOutputVariables(execution)
        .forEach(headerOutputVariable -> performExecuteHeaderOutputVariables(execution, headerOutputVariable, response));
    } catch (ResourceAccessException e) {
      throwExternalRequestException(tenant, url, null, null, e.getMessage(), e);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      throwExternalRequestException(tenant, url, e.getResponseHeaders(), e.getStatusCode(), e.getResponseBodyAsString(), e);
    }
  }

}
