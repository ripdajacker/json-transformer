package dk.mehmedbasic.jackson.transform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import dk.mehmedbasic.jackson.JacksonConverter;
import dk.mehmedbasic.tree.BaseNode;
import dk.mehmedbasic.tree.NodeList;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Adds a value
 */
final class AddValue extends TransformationFunction {

  private final JsonNodeType type;
  private final Object value;
  private final String name;

  public AddValue(String name, JsonNodeType type, Object value) {
    this.name = name;
    this.type = Objects.requireNonNullElse(type, JsonNodeType.NULL);
    this.value = value;
  }

  @Override
  public void apply(NodeList<JsonNode> root) {
    BaseNode<JsonNode> newChild;
    if (value instanceof BaseNode node) {
      // Verbatim addition
      newChild = node;
      newChild.getIdentifier().setName(name);
    } else {
      newChild = createNode();
      newChild.getIdentifier().setName(name);
      if (value != null) {
        var mapper = new ObjectMapper();
        var valueNode = mapper.valueToTree(value);

        if (newChild.getValue().isArray()) {
          newChild.addChild(JacksonConverter.convertToTransformable(null, valueNode));
        } else if (newChild.getValue().isValueNode()) {
          newChild = JacksonConverter.convertToTransformable(null, valueNode);
        }
      }
    }

    LinkedHashSet<BaseNode<JsonNode>> roots = new LinkedHashSet<>(root.getNodes());
    for (BaseNode<JsonNode> node : roots) {
      node.addChild(newChild);
    }
  }

  private BaseNode<JsonNode> createNode() {
    JsonNode inner = switch (type) {
      case ARRAY -> JsonNodeFactory.instance.arrayNode();
      case OBJECT -> JsonNodeFactory.instance.objectNode();
      case BOOLEAN -> JsonNodeFactory.instance.booleanNode(false);
      case STRING -> JsonNodeFactory.instance.textNode("");
      case NUMBER -> JsonNodeFactory.instance.numberNode(0);
      default -> JsonNodeFactory.instance.nullNode();
    };
    return new BaseNode<>(inner);
  }
}
