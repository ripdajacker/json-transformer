package dk.mehmedbasic.css.selector;

import dk.mehmedbasic.tree.BaseNode;
import dk.mehmedbasic.tree.NodeFilter;
import dk.mehmedbasic.tree.NodeId;

/**
 * A filter that works on the identifier of the node.
 */
abstract class NodeIdFilter<T> extends NodeFilter<T> {

  @Override
  public boolean test(BaseNode<T> node, Integer index) {
    return apply(node.getIdentifier());
  }

  public abstract boolean apply(NodeId identifier);
}
