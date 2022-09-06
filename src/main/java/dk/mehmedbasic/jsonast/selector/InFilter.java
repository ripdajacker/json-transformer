package dk.mehmedbasic.jsonast.selector;

import dk.mehmedbasic.jsonast.BaseNode;
import java.util.Collection;

/**
 * A filter for checking if a node is in a collection.
 */
public class InFilter extends NodeFilter {

  private final Collection<BaseNode> nodes;

  public InFilter(Collection<BaseNode> nodes) {
    this.nodes = nodes;
  }

  @Override
  public boolean apply(BaseNode node, Integer index) {
    return nodes.contains(node);
  }
}
