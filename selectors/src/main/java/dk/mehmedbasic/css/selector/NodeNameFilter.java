package dk.mehmedbasic.css.selector;

import dk.mehmedbasic.tree.NodeId;
import java.util.Objects;

/**
 * Node name filter.
 */
public class NodeNameFilter<T> extends NodeIdFilter<T> {

  private final String nodeName;

  public NodeNameFilter(String nodeName) {
    this.nodeName = nodeName;
  }

  @Override
  public boolean apply(NodeId identifier) {
    return Objects.equals(identifier.getName(), nodeName);
  }
}
