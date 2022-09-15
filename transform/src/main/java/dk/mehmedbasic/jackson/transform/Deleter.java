package dk.mehmedbasic.jackson.transform;

import dk.mehmedbasic.jackson.BaseNode;
import dk.mehmedbasic.jackson.JsonDocument;
import dk.mehmedbasic.jackson.JsonNodes;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Deletes child nodes
 */
public final class Deleter extends TransformationFunction {

  private String name;
  private int index = -1;

  public Deleter(String name) {
    this.name = name;
  }

  public Deleter(int index) {
    this.index = index;
  }

  @Override
  public void apply(JsonDocument document, JsonNodes root) {
    if (name != null && name.length() > 0) {
      LinkedHashSet<BaseNode> nodes = new LinkedHashSet<>(root.getRoots());
      for (BaseNode node : nodes) {
        BaseNode get = node.get(name);
        TransformationFunction.nodeChanged(root, get);
        node.removeNode(get);
      }
    } else if (index >= 0) {
      ArrayList<BaseNode> nodes = new ArrayList<>(root.getRoots());
      for (BaseNode node : nodes) {
        if (node.isObject() || node.isArray()) {
          BaseNode get = node.get(index);
          TransformationFunction.nodeChanged(root, get);
          node.removeNode(get);
        }
      }
    }
  }
}
