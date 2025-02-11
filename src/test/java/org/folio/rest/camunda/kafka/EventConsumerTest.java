package org.folio.rest.camunda.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.stream.Stream;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.folio.spring.messaging.model.Event;
import org.folio.spring.tenant.storage.ThreadLocalStorage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

  @MockitoSpyBean
  private ObjectMapper objectMapper;

  @MockitoBean
  private RuntimeService runtimeService;

  @Mock
  private MessageCorrelationBuilder messageCorrelationBuilder;

  @Mock
  private ProcessInstance processInstance;

  @InjectMocks
  private EventConsumer eventConsumer;

  // Provide a bean for `@MockitoSpyBean` above to work without requiring a full spring boot runner.
  @Configuration
  static class Config {

    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  @ParameterizedTest
  @MethodSource("eventStream")
  @SuppressWarnings("unchecked")
  void testReceive(Event event) {
    try (MockedStatic<ThreadLocalStorage> utility = Mockito.mockStatic(ThreadLocalStorage.class)) {
      doReturn(processInstance).when(messageCorrelationBuilder).correlateStartMessage();
      doReturn(messageCorrelationBuilder).when(messageCorrelationBuilder).setVariables(anyMap());
      doReturn(messageCorrelationBuilder).when(messageCorrelationBuilder).tenantId(anyString());
      doReturn(messageCorrelationBuilder).when(runtimeService).createMessageCorrelation(anyString());

      doReturn(new HashMap<String, Object>()).when(objectMapper).convertValue(any(JsonNode.class), any(TypeReference.class));

      eventConsumer.receive(event);

      utility.verify(() -> {
        ThreadLocalStorage.setTenant(event.getTenant());
      });

      Mockito.verify(messageCorrelationBuilder).correlateStartMessage();
    }
  }

  static Stream<Event> eventStream() {
    return Stream.of(new Event[] {
        new Event(
          "triggerId",
          "pathPattern",
          "method",
          "tenant",
          "path"
        ),
        new Event(
          "triggerId",
          "pathPattern",
          "method",
          "tenant",
          "path",
          new HashMap<String, String>()
        )
    });
  }

}
