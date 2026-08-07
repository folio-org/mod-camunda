package org.folio.rest.camunda.service;

import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.SubfieldImpl;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * De-serializer for Subfield.
 *
 * This is intended to be used with Jackson 3 when de-serializing a SimpleModule.
 */
public class SubfieldDeserializer extends ValueDeserializer<Subfield> {

  private final JsonMapper mapper;

  /**
   * Initializer.
   */
  public SubfieldDeserializer(JsonMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Subfield deserialize(JsonParser jp, DeserializationContext ctxt) throws JacksonException {

    final ObjectReadContext oc = jp.objectReadContext();
    final JsonNode node = oc.readTree(jp);

    return mapper.treeToValue(node, SubfieldImpl.class);
  }

}
