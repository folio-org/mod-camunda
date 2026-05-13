package org.folio.rest.camunda.service;

import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.SubfieldImpl;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;

/**
 * Deserializer for Subfield.
 *
 * This is intended to be used with Jackson when deserializing a SimpleModule.
 */
public class SubfieldDeserializer extends ValueDeserializer<Subfield> {

  private final ObjectMapper objectMapper;

  /**
   * Initializer.
   */
  public SubfieldDeserializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Subfield deserialize(JsonParser jp, DeserializationContext ctxt) throws JacksonException {

    final ObjectReadContext oc = jp.objectReadContext();
    final JsonNode node = oc.readTree(jp);

    return objectMapper.treeToValue(node, SubfieldImpl.class);
  }

}
