package dk.mehmedbasic.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * NodeList.
 */
public final class NodeList<T> {

  private final List<BaseNode<T>> nodes;

  public NodeList(List<BaseNode<T>> nodes) {
    this.nodes = nodes;
  }

  public NodeList<T> parent() {
    return new NodeList<>(this.nodes.stream().map(BaseNode::getParent).toList());
  }

  public List<BaseNode<T>> getNodes() {
    return nodes;
  }

  public NodeList<T> filter(NodeFilter<T> predicate) {
    var resulting = new ArrayList<BaseNode<T>>(nodes.size());

    for (int i = 0; i < nodes.size(); i++) {
      BaseNode<T> node = nodes.get(i);
      if (predicate.test(node, i)) {
        resulting.add(node);
      }
    }

    return new NodeList<>(resulting);
  }
}
