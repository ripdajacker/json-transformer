package dk.mehmedbasic.jsonast.transform;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonNodes;
import dk.mehmedbasic.jsonast.JsonValueNode;
import java.util.function.Consumer;

/**
 * Manipulates values
 */
final class Manipulator extends TransformationFunction {

  private final ManipulateValueFunction function;
  private final Consumer<JsonValueNode> closure;
  private final String childName;
  private final int childIndex;

  public Manipulator(int childIndex, String childName, ManipulateValueFunction function) {
    this.childIndex = childIndex;
    this.childName = childName;
    this.function = function;
    this.closure = null;
  }

  public Manipulator(int childIndex, String childName, Consumer<JsonValueNode> closure) {
    this.childIndex = childIndex;
    this.childName = childName;
    this.closure = closure;
    this.function = null;
  }

  @Override
  public void apply(JsonDocument document, JsonNodes root) {
    for (BaseNode node : root) {
      if (childIndex >= 0) {
        applyManipulation(node.get(childIndex));
      } else if (childName != null) {
        applyManipulation(node.get(childName));
      } else {
        applyManipulation(node);
      }
    }
  }

  private void applyManipulation(BaseNode node) {
    if (node == null || !node.isValueNode()) {
      throw new IllegalArgumentException("Expected value node, but got: " + node);
    }

    if (closure != null) {
      closure.accept((JsonValueNode) node);
    } else if (function != null) {
      function.apply((JsonValueNode) node);
    }
  }
}
