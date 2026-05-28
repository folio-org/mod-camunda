package org.folio.rest.camunda.utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.folio.spring.test.helper.MapperHelper;
import org.springframework.http.ResponseEntity;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Test utility for various input methods for parameterized testing of
 * utilities.
 */
public class TestUtility {

  private static final JsonMapper mapper = MapperHelper.build();

  /**
   * Prevent utility from being directly instantiated.
   */
  private TestUtility() {
    // This should do nothing.
  }

  /**
   * Build input from file as class type wrapped by ResponseEntity.
   *
   * @param <T> generic input type
   * @param path path to mock resource JSON
   * @param valueType type of generic object to map to
   *
   * @return ResponseEntity for a expected type to test with
   */
  public static <T> ResponseEntity<T> i(String path, Class<T> valueType) {
    return ResponseEntity.ofNullable(mapper.readValue(new File("src/test/resources/" + path), valueType));
  }

  /**
   * Build input from file as a String
   *
   * @param path path to mock resource
   * @return String of the input file as UTF-8
   * @throws IOException when reading file
   */
  public static String i(String path) throws IOException {
    return IOUtils.resourceToString(path, StandardCharsets.UTF_8);
  }

  /**
   * Build input from two files as an array of String.
   *
   * @param path input file
   * @param additionalPath path to additional file
   * @return array of strings from the file input
   * @throws IOException
   */
  public static String[] i(String path, String additionalPath) throws IOException {
    return new String[] {
      IOUtils.resourceToString(path, StandardCharsets.UTF_8),
      IOUtils.resourceToString(additionalPath, StandardCharsets.UTF_8)
    };
  }

  /**
   * Build input from a JSON array file as a List<String>.
   *
   * @param path path to input file that is a JSON array
   * @return list of JSON array entries as String
   * @throws IOException when reading file or object mapping fails
   */
  public static List<String> il(String path) throws IOException {
    String json = i(path);
    List<String> marcjson = new ArrayList<>();

    for (JsonNode n : mapper.readTree(json)) {
      marcjson.add(n.toString());
    }

    return marcjson;
  }

  /**
   * Object map a JSON String to JsonNode catching any exceptions.
   *
   * @param json JSON String
   * @return JsonNode
   */
  public static JsonNode treeNode(String json) {
    try {
      return mapper.readTree(json);
    } catch (JacksonException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

  /**
   * Object map list of String to list of JsonNode.
   *
   * @param json list of JSON strings
   * @return list of JsonNode
   */
  public static List<JsonNode> treeList(List<String> json) {
    return json.stream().map(n -> treeNode(n)).toList();
  }

}
