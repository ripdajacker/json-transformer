package dk.mehmedbasic.jsonast.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonArrayNode;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonObjectNode;
import dk.mehmedbasic.jsonast.JsonValueNode;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Converts jackson nodes to dk.mehmedbasic.jsonast nodes.
 */
public class JacksonConverter {

  private final static class Converter {

    private final ConversionNamingStrategy strategy;

    public Converter(ConversionNamingStrategy strategy) {
      this.strategy = Objects.requireNonNullElseGet(strategy, DefaultNamingStrategy::new);
    }

    public JsonDocument asTransformable(JsonNode root) {
      JsonDocument document = new JsonDocument();
      document.addRoot(convertToTransformable(null, root));
      return document;
    }

    public JsonNode asJacksonNode(JsonDocument document) {
      return convertToJackson(document.iterator().next());
    }

    private JsonNode convertToJackson(BaseNode baseNode) {
      if (baseNode.isArray()) {
        ArrayNode result = new ArrayNode(JsonNodeFactory.instance);
        JsonArrayNode arrayNode = (JsonArrayNode) baseNode;
        for (BaseNode childNode : arrayNode.getChildren()) {
          JsonNode jackson = convertToJackson(childNode);
          if (jackson instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) jackson;
            strategy.toJacksonInArray(childNode)
                .forEach(pair -> objectNode.put(pair.name(), pair.value()));
          }

          result.add(jackson);
        }

        return result;
      }

      if (baseNode.isValueNode()) {
        JsonValueNode valueNode = (JsonValueNode) baseNode;
        if (baseNode.isInt()) {
          return new IntNode(valueNode.intValue());
        } else if (baseNode.isDouble()) {
          return new DoubleNode(valueNode.doubleValue());
        } else if (baseNode.isBoolean()) {
          if (valueNode.booleanValue()) {
            return BooleanNode.TRUE;
          } else {
            return BooleanNode.FALSE;
          }
        } else if (baseNode.isString()) {
          return new TextNode(valueNode.stringValue());
        }

        return NullNode.getInstance();
      }

      if (baseNode.isObject()) {
        ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
        JsonObjectNode objectNode = (JsonObjectNode) baseNode;

        for (BaseNode childNode : objectNode.getChildren()) {
          result.putIfAbsent(strategy.toJacksonName(childNode), convertToJackson(childNode));
        }

        return result;
      }

      throw new IllegalArgumentException("Unknown node type: " + baseNode);
    }

    /**
     * Converts the given node.
     *
     * @param name the name of the node.
     * @param source the source Jackson node.
     * @return the converted node.
     */
    private BaseNode convertToTransformable(String name, JsonNode source) {
      if (source.isObject()) {
        JsonObjectNode result = new JsonObjectNode();
        ObjectNode objectNode = (ObjectNode) source;

        result.setIdentifier(strategy.toTransformableName(name, result));
        result.getIdentifier().getClasses().add("object");

        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
        while (fields.hasNext()) {
          Map.Entry<String, JsonNode> entry = fields.next();
          result.addChild(convertToTransformable(entry.getKey(), entry.getValue()));
        }

        return result;
      } else if (source.isArray()) {
        JsonArrayNode result = new JsonArrayNode();
        ArrayNode arrayNode = (ArrayNode) source;

        result.setIdentifier(strategy.toTransformableName(name, result));
        result.getIdentifier().getClasses().add("array");

        for (JsonNode node : arrayNode) {
          result.addChild(convertToTransformable(null, node));
        }

        return result;
      } else if (source.isValueNode()) {
        JsonValueNode result = new JsonValueNode(null);
        result.setIdentifier(strategy.toTransformableName(name, result));
        if (name.equals("@version")) {
          result.getIdentifier().addClass("sysclass_version");
        }

        result.getIdentifier().getClasses().add("value");

        if (source.isBoolean()) {
          result.getIdentifier().getClasses().add("boolean");
          result.setValue(source.booleanValue());
        } else if (source.isInt()) {
          result.getIdentifier().getClasses().add("int");
          result.setValue(source.intValue());
        } else if (source.isTextual()) {
          result.getIdentifier().getClasses().add("string");
          result.setValue(source.textValue());
        } else if (source.isDouble()) {
          result.getIdentifier().getClasses().add("double");
          result.setValue(source.doubleValue());
        }

        return result;
      }

      return new JsonValueNode(null);
    }
  }

  /**
   * Converts a Jackson node to a JsonDocument.
   *
   * @param root the root to convert.
   * @param namingStrategy the naming strategy.
   * @return the converted document.
   */
  public static JsonDocument asTransformable(JsonNode root,
      ConversionNamingStrategy namingStrategy) {
    return new Converter(namingStrategy).asTransformable(root);
  }

  /**
   * Converts a Jackson node to a JsonDocument.
   *
   * @param root the root to convert.
   * @return the converted document.
   */
  public static JsonDocument asTransformable(JsonNode root) {
    return JacksonConverter.asTransformable(root, null);
  }

  /**
   * Converts the transformable into a Jackson tree.
   *
   * @param document the document
   * @param namingStrategy the naming strategy.
   * @return the resulting node.
   */
  public static JsonNode asJacksonNode(JsonDocument document,
      ConversionNamingStrategy namingStrategy) {
    return new Converter(namingStrategy).asJacksonNode(document);
  }

  /**
   * Converts the transformable into a Jackson tree.
   *
   * @param document the document
   * @return the resulting node.
   */
  public static JsonNode asJacksonNode(JsonDocument document) {
    return JacksonConverter.asJacksonNode(document, null);
  }
}
