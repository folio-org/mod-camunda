package org.folio.rest.delegate;

import static org.camunda.spin.Spin.JSON;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.folio.rest.model.OkapiRequest;
import org.folio.rest.model.OkapiResponse;
import org.folio.rest.service.OkapiRequestService;
import org.folio.rest.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class TestCreateForEachDelegate extends AbstractRuntimeDelegate {

  @Autowired
  private StreamService streamService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  OkapiRequestService okapiRequestService;

  private Expression endpoint;

  private Expression target;

  private Expression source;

  private Expression uniqueBy;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    FlowElement bpmnModelElemen = execution.getBpmnModelElementInstance();
    String delegateName = bpmnModelElemen.getName();
    if (endpoint != null && target != null && source != null) {
      String tenant = execution.getTenantId();
      String token = (String) execution.getVariable("okapiToken");
      String targetValue = target.getValue(execution).toString();
      String sourceValue = source.getValue(execution).toString();
      String uniqueByValue = uniqueBy.getValue(execution).toString();
      String endpointValue = endpoint.getValue(execution).toString();

      System.out.println(String.format("%s STARTED", delegateName));

      streamService.map(d -> {
        String returnData = d;
        try {
          ObjectNode destNode = (ObjectNode) objectMapper.readTree(d);
          JsonNode srcNode = getSourceNode(sourceValue, destNode);
          ArrayNode ids = objectMapper.createArrayNode();
          if (srcNode.isArray()) {
            srcNode.forEach(s -> {
              OkapiResponse res = null;
              JsonNode rNode = null;
              try {
                if (uniqueByValue.equals("NO_VALUE")) {
                  res = createEntity(tenant, token, endpointValue, s);
                  rNode = objectMapper.readTree(res.getBody());
                } else {
                  if (s.get(uniqueByValue) != null) {
                    res = getEntity(tenant, token, endpointValue, uniqueByValue, s.get(uniqueByValue).asText());
                    JsonNode rNodes = objectMapper.readTree(res.getBody());
                    if (rNodes.get("totalRecords").asInt() == 0) {
                      res = createEntity(tenant, token, endpointValue, s);
                      rNode = objectMapper.readTree(res.getBody());
                    } else {
                      rNode = rNodes.fields().next().getValue().get(0);
                    }
                  }
                }
                if (rNode != null && rNode.get(targetValue) != null) {
                  String newId = rNode.get(targetValue).toString();
                  ids.add(newId.replace("\"", ""));
                  destNode.set(sourceValue, ids);
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            });
            returnData = destNode.toString();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        return returnData;
      });
    }
  }

  private JsonNode getSourceNode(String sourceValue, ObjectNode destNode) throws IOException {
    JsonNode sourceNode = destNode;
    String[] sourceParts = sourceValue.split(".");
    System.out.println(sourceParts);
    for (String s: sourceParts) {
      if(sourceNode != null) {
        sourceNode = sourceNode.get(s);
      }
    }
    return destNode.get(sourceValue);
  }

  private OkapiResponse createEntity(String tenant, String token, String endpointValue, JsonNode d) {
    OkapiRequest okapiRequest = new OkapiRequest();
    okapiRequest.setTenant(tenant);
    okapiRequest.setRequestUrl(endpointValue);
    okapiRequest.setRequestMethod("POST");
    okapiRequest.setRequestContentType("application/json");
    okapiRequest.setRequestPayload(JSON(d));
    okapiRequest.setOkapiToken(token);
    return okapiRequestService.okapiRestCall(okapiRequest);
  }

  private OkapiResponse getEntity(String tenant, String token, String endpointValue, String uniqueKey, String value) {
    OkapiRequest okapiRequest = new OkapiRequest();
    okapiRequest.setTenant(tenant);
    okapiRequest.setRequestUrl(String.format("%s?query=(%s=\"%s\")", endpointValue, uniqueKey.replaceAll("/\\//", ""), value));
    okapiRequest.setRequestMethod("GET");
    okapiRequest.setRequestPayload(JSON("{}"));
    okapiRequest.setRequestContentType("application/json");
    okapiRequest.setOkapiToken(token);
    return okapiRequestService.okapiRestCall(okapiRequest);
  }

  public void setEndpoint(Expression endpoint) {
    this.endpoint = endpoint;
  }

  public void setTarget(Expression target) {
    this.target = target;
  }

  public void setSource(Expression source) {
    this.source = source;
  }

  public void setUniqueBy(Expression uniqueBy) {
    this.uniqueBy = uniqueBy;
  }

}
