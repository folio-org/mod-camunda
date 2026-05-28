package org.folio.rest.camunda.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.compress.compressors.gzip.ExtraField;
import org.folio.rest.camunda.service.SubfieldDeserializer;
import org.marc4j.MarcException;
import org.marc4j.MarcJsonReader;
import org.marc4j.MarcJsonWriter;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.node.ArrayNode;

public class MarcUtility {

  private static final Logger logger = LoggerFactory.getLogger(MarcUtility.class);

  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  protected static final JsonMapper mapper;

  static {
    final SimpleModule module = new SimpleModule();

    mapper = JsonMapper
      .builderWithJackson2Defaults()
      .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES, true)
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, true)
      .changeDefaultPropertyInclusion(incl -> incl
        .withValueInclusion(JsonInclude.Include.NON_NULL)
        .withContentInclusion(JsonInclude.Include.NON_NULL)
      )
      .findAndAddModules()
      .build();

    module.addDeserializer(Subfield.class, new SubfieldDeserializer(mapper));
    module.setDeserializerModifier(new ValueDeserializerModifier() {
      @Override
      public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
        BeanDescription.Supplier beanDescRef, ValueDeserializer<?> deserializer) {
          if (ExtraField.SubField.class.isAssignableFrom(beanDescRef.getBeanClass())) {
            return new SubfieldDeserializer(mapper);
          }

          return deserializer;
        }
      }
    );
  }

  /**
   * Initializer.
   */
  private MarcUtility() {
  }

  /**
   * Split raw MARC to MARC JSON.
   *
   * @param rawMarc The raw MARC.
   *
   * @return The MARC JSON.
   *
   * @throws IOException On error.
   */
  public static List<String> splitRawMarcToMarcJsonRecords(String rawMarc) throws IOException {
    List<String> records = new ArrayList<>();
    try (InputStream in = new ByteArrayInputStream(rawMarc.getBytes(DEFAULT_CHARSET))) {
      final MarcStreamReader reader = new MarcStreamReader(in, DEFAULT_CHARSET.name());
      while (reader.hasNext()) {
        records.add(recordToMarcJson(reader.next()));
      }
    }

    return records;
  }

  public static String addFieldToMarcJson(String marcJson, String fieldJson)
      throws MarcException, IOException {
    JsonNode fieldNode = mapper.readTree(fieldJson);
    MarcFactory factory = MarcFactory.newInstance();
    Record marcRecord = marcJsonToRecord(marcJson);

    String tag = fieldNode.get("tag").asString();

    DataField field = factory.newDataField();
    field.setTag(tag);

    if (fieldNode.has("indicator1")) {
      char indicator1 = fieldNode.get("indicator1").asString().charAt(0);
      field.setIndicator1(indicator1);
    }

    if (fieldNode.has("indicator2")) {
      char indicator2 = fieldNode.get("indicator2").asString().charAt(0);
      field.setIndicator2(indicator2);
    }

    ArrayNode subfields = (ArrayNode) fieldNode.get("subfields");

    subfields.forEach(subfieldNode -> {
      char code = subfieldNode.get("code").asString().charAt(0);
      String data = subfieldNode.get("data").asString();
      Subfield subfield = factory.newSubfield();
      subfield.setCode(code);
      subfield.setData(data);
      field.addSubfield(subfield);
    });

    marcRecord.addVariableField(field);

    recalculateLeader(marcRecord);

    return recordToMarcJson(marcRecord);
  }

  public static String updateControlNumberField(String marcJson, String data)
      throws MarcException, IOException {
    Record marcRecord = marcJsonToRecord(marcJson);
    if (Objects.nonNull(marcRecord.getControlNumberField())) {
      marcRecord.getControlNumberField().setData(data);
    }
    else {
      ControlField controlField = MarcFactory.newInstance().newControlField("001");
      controlField.setData(data);
      marcRecord.addVariableField(controlField);
    }
    recalculateLeader(marcRecord);

    return recordToMarcJson(marcRecord);
  }

  public static String marcJsonToRawMarc(String marcJson)
      throws MarcException, IOException {
    return recordToRawMarc(marcJsonToRecord(marcJson));
  }

  public static String rawMarcToMarcJson(String rawMarc)
      throws MarcException, IOException {
    return recordToMarcJson(rawMarcToRecord(rawMarc));
  }

  public static String getFieldsFromRawMarc(String rawMarc, String[] tags)
      throws JacksonException, MarcException, IOException {
    return getRecordFields(rawMarcToRecord(rawMarc), tags);
  }

  public static String getFieldsFromMarcJson(String marcJson, String[] tags)
      throws JacksonException, MarcException, IOException {
    return getRecordFields(marcJsonToRecord(marcJson), tags);
  }

  private static Record marcJsonToRecord(String marcJson)
      throws MarcException, IOException {
    logger.debug("Attempting to read MARC JSON to Record: {}", marcJson);
    try (InputStream in = new ByteArrayInputStream(marcJson.getBytes())) {
      final MarcJsonReader reader = new MarcJsonReader(in);
      if (reader.hasNext()) {
        return reader.next();
      }
    }

    throw new MarcException("No record found");
  }

  private static Record rawMarcToRecord(String rawMarc)
      throws MarcException, IOException {
    logger.debug("Attempting to read raw MARC to Record: {}", rawMarc);
    try (InputStream in = new ByteArrayInputStream(rawMarc.getBytes(DEFAULT_CHARSET))) {
      final MarcStreamReader reader = new MarcStreamReader(in, DEFAULT_CHARSET.name());
      if (reader.hasNext()) {
        return reader.next();
      }
    }

    throw new MarcException("No record found");
  }

  private static String recordToMarcJson(Record marcRecord) throws IOException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      final MarcJsonWriter writer = new MarcJsonWriter(out);
      writer.write(marcRecord);
      writer.close();
      return out.toString();
    }
  }

  private static String recordToRawMarc(Record marcRecord) throws IOException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      final MarcStreamWriter writer = new MarcStreamWriter(out);
      writer.write(marcRecord);
      writer.close();
      return out.toString();
    }
  }

  private static String getRecordFields(Record marcRecord, String[] tags) throws JacksonException {
    List<VariableField> fields = marcRecord.getVariableFields(tags);
    return mapper.writerWithDefaultPrettyPrinter()
      .writeValueAsString(fields);
  }

  private static void recalculateLeader(Record marcRecord) throws IOException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      MarcWriter streamWriter = new MarcStreamWriter(out, DEFAULT_CHARSET.name());
      // use stream writer to recalculate leader
      streamWriter.write(marcRecord);
      streamWriter.close();
    }
  }

}
