package dk.mehmedbasic.jsonast;

import java.util.ArrayList;
import java.util.List;

/**
 * A JsonArray.
 */
public class JsonArrayNode extends BaseNode {

  private final List<BaseNode> children = new ArrayList<>();

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public BaseNode get(int index) {
    if (index < children.size()) {
      return children.get(index);
    }

    return null;
  }

  @Override
  public void addChild(BaseNode node) {
    super.addChild(node);
    children.add(node);
  }

  @Override
  public void removeNode(BaseNode node) {
    if (children.contains(node)) {
      children.remove(node);
      super.removeNode(node);
    }
  }

  public int size() {
    return children.size();
  }

  public List<BaseNode> getChildren() {
    return children;
  }

  @Override
  public String toString() {
    return "JsonArrayNode[" + getIdentifier() + "][" + getChildren() + "]";
  }
}
