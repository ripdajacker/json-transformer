package dk.mehmedbasic.css.selector;

import dk.mehmedbasic.tree.BaseNode;
import dk.mehmedbasic.tree.NodeFilter;
import java.util.Collection;

/**
 * A filter for checking if a node is in a collection.
 */
public class InFilter<T> extends NodeFilter<T> {

  private final Collection<BaseNode<T>> nodes;

  public InFilter(Collection<BaseNode<T>> nodes) {
    this.nodes = nodes;
  }

  @Override
  public boolean test(BaseNode<T> node, Integer index) {
    return nodes.contains(node);
  }
}
