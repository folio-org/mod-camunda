package org.folio.rest.camunda.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.stream.Stream;
import org.folio.spring.messaging.model.Event;
import org.folio.spring.tenant.storage.ThreadLocalStorage;
import org.folio.spring.test.helper.MapperHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.runtime.MessageCorrelationBuilder;
import org.operaton.bpm.engine.runtime.ProcessInstance;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class EventConsumerTest {

  private JsonMapper mapper;

  @MockitoBean
  private RuntimeService runtimeService;

  @Mock
  private MessageCorrelationBuilder messageCorrelationBuilder;

  @Mock
  private ProcessInstance processInstance;

  private EventConsumer eventConsumer;

  @BeforeEach
  void beforeEach() {
    mapper = Mockito.spy(MapperHelper.build());
    eventConsumer = Mockito.spy(new EventConsumer(runtimeService, mapper));
  }

  @ParameterizedTest
  @MethodSource("provideEventStream")
  @SuppressWarnings("unchecked")
  void testReceive(Event event) {
    try (MockedStatic<ThreadLocalStorage> utility = Mockito.mockStatic(ThreadLocalStorage.class)) {
      doReturn(processInstance).when(messageCorrelationBuilder).correlateStartMessage();
      doReturn(messageCorrelationBuilder).when(messageCorrelationBuilder).setVariables(anyMap());
      doReturn(messageCorrelationBuilder).when(messageCorrelationBuilder).tenantId(anyString());
      doReturn(messageCorrelationBuilder).when(runtimeService).createMessageCorrelation(anyString());

      doReturn(new HashMap<String, Object>()).when(mapper).convertValue(any(JsonNode.class), any(TypeReference.class));

      eventConsumer.receive(event);

      utility.verify(() -> {
        ThreadLocalStorage.setTenant(event.getTenant());
      });

      Mockito.verify(messageCorrelationBuilder).correlateStartMessage();
    }
  }

  static Stream<Event> provideEventStream() {
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
