package dk.mehmedbasic.jsonast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.mehmedbasic.jsonast.conversion.InlineIdsNamingStrategy;
import dk.mehmedbasic.jsonast.conversion.JacksonConverter;
import java.io.IOException;
import java.io.InputStream;

/**
 * A json document.
 * <p>
 * This is used as the base class for selection and transformation.
 */
public class JsonDocument extends JsonNodes {

  public JsonDocument() {
    setDocument(this);
  }

  /**
   * Creates a value node.
   *
   * @return the node.
   */
  public static JsonValueNode createValueNode() {
    JsonValueNode node = new JsonValueNode(null);
    node.getIdentifier().addClass("value");
    return node;
  }

  /**
   * Creates a text node.
   *
   * @return the node.
   */
  public static JsonValueNode createTextNode(String value) {
    JsonValueNode node = createValueNode();
    node.getIdentifier().addClass("string");
    node.setValue(value);
    return node;
  }

  /**
   * Creates a number value node.
   *
   * @return the node.
   */
  public static JsonValueNode createNumberNode(double value) {
    JsonValueNode node = createValueNode();
    node.getIdentifier().addClass("double");
    node.setValue(value);
    return node;
  }

  /**
   * Creates a boolean value node.
   *
   * @return the node.
   */
  public static JsonValueNode createBooleanNode(boolean value) {
    JsonValueNode node = createValueNode();
    node.setValue(value);
    node.getIdentifier().addClass("boolean");
    return node;
  }

  /**
   * Creates an array node.
   *
   * @return the node.
   */
  public static JsonArrayNode createArrayNode() {
    JsonArrayNode node = new JsonArrayNode();
    node.getIdentifier().addClass("array");
    return node;
  }

  /**
   * Creates an object node.
   *
   * @return the node.
   */
  public static JsonObjectNode createObjectNode() {
    JsonObjectNode node = new JsonObjectNode();
    node.getIdentifier().addClass("object");
    return node;
  }

  /**
   * Parses a JsonDocument from the given input.
   *
   * @param inputStream the stream to parse.
   * @return the resulting JsonDocument.
   */
  public static JsonDocument parse(InputStream inputStream) throws IOException {
    JsonNode node = new ObjectMapper().readTree(inputStream);
    return JacksonConverter.asTransformable(node, new InlineIdsNamingStrategy());
  }

  /**
   * Parses a JsonDocument from the given input.
   *
   * @param content the string to parse.
   * @return the resulting JsonDocument.
   */
  public static JsonDocument parse(String content) throws JsonProcessingException {
    JsonNode node = new ObjectMapper().readTree(content);
    return JacksonConverter.asTransformable(node, new InlineIdsNamingStrategy());
  }
}
