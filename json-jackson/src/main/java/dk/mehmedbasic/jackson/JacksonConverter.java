package dk.mehmedbasic.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import dk.mehmedbasic.tree.BaseNode;
import java.util.Iterator;
import java.util.Map;

/**
 * Converts jackson nodes to dk.mehmedbasic.jsonast nodes.
 */
public class JacksonConverter {

  public static JsonNode convertToJackson(BaseNode<JsonNode> baseNode) {
    JsonNode innerValue = baseNode.getValue();
    if (innerValue.isArray()) {
      ArrayNode result = new ArrayNode(JsonNodeFactory.instance);
      for (BaseNode<JsonNode> childNode : baseNode.getChildren()) {
        JsonNode jackson = convertToJackson(childNode);
        result.add(jackson);
      }

      return result;
    } else if (innerValue.isValueNode()) {
      if (innerValue.isInt()) {
        return new IntNode(innerValue.intValue());
      } else if (innerValue.isDouble()) {
        return new DoubleNode(innerValue.doubleValue());
      } else if (innerValue.isBoolean()) {
        if (innerValue.booleanValue()) {
          return BooleanNode.TRUE;
        } else {
          return BooleanNode.FALSE;
        }
      } else if (innerValue.isTextual()) {
        return new TextNode(innerValue.textValue());
      }

      return NullNode.getInstance();
    } else if (innerValue.isObject()) {
      ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
      for (BaseNode<JsonNode> childNode : baseNode.getChildren()) {
        result.putIfAbsent(childNode.getIdentifier().getName(), convertToJackson(childNode));
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
  public static BaseNode<JsonNode> convertToTransformable(String name, JsonNode source) {
    BaseNode<JsonNode> result = new BaseNode<>(source);
    result.getIdentifier().setName(name);
    if (source.isObject()) {
      result.getIdentifier().getClasses().add("object");

      ObjectNode objectNode = (ObjectNode) source;
      Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> entry = fields.next();
        result.addChild(convertToTransformable(entry.getKey(), entry.getValue()));
      }
    } else if (source.isArray()) {
      ArrayNode arrayNode = (ArrayNode) source;
      result.getIdentifier().getClasses().add("array");

      for (JsonNode node : arrayNode) {
        result.addChild(convertToTransformable(null, node));
      }
    } else if (source.isValueNode()) {
      if (name.equals("@version")) {
        result.getIdentifier().addClass("sysclass_version");
      }

      result.getIdentifier().getClasses().add("value");

      if (source.isBoolean()) {
        result.getIdentifier().getClasses().add("boolean");
      } else if (source.isInt()) {
        result.getIdentifier().getClasses().add("int");
      } else if (source.isTextual()) {
        result.getIdentifier().getClasses().add("string");
      } else if (source.isDouble()) {
        result.getIdentifier().getClasses().add("double");
      }
    }

    return result;
  }
}
