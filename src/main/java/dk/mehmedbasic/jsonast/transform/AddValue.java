package dk.mehmedbasic.jsonast.transform;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonNodes;
import dk.mehmedbasic.jsonast.JsonType;
import dk.mehmedbasic.jsonast.JsonValueNode;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Adds a value
 */
final class AddValue extends TransformationFunction {

  private final JsonType type;
  private final Object value;
  private final String name;

  public AddValue(String name, JsonType type, Object value) {
    this.name = name;
    this.type = Objects.requireNonNullElse(type, JsonType.Value);
    this.value = value;
  }

  @Override
  public void apply(JsonDocument document, JsonNodes root) {
    BaseNode newChild;
    if (value instanceof BaseNode node) {
      // Verbatim addition
      newChild = node;
      newChild.getIdentifier().setName(name);
    } else {
      newChild = createNode();

      assert newChild != null;
      newChild.getIdentifier().setName(name);
      if (value != null) {
        if (newChild.isArray()) {
          JsonValueNode valueNode = JsonDocument.createValueNode();
          valueNode.setRawValue(value);
          // TODO typed
          newChild.addChild(valueNode);
        } else if (newChild.isValueNode()) {
          // TODO typed add
          ((JsonValueNode) newChild).setRawValue(value);
        }
      }
    }

    LinkedHashSet<BaseNode> roots = new LinkedHashSet<>(root.getRoots());
    for (BaseNode node : roots) {
      node.addChild(newChild);

      TransformationFunction.nodeChanged(root, node);
      TransformationFunction.nodeChanged(root, newChild);
    }
  }

  private BaseNode createNode() {
    return switch (type) {
      case Array -> JsonDocument.createArrayNode();
      case Object -> JsonDocument.createObjectNode();
      case Value -> JsonDocument.createValueNode();
    };
  }
}
