package dk.mehmedbasic.jsonast.selector;

import dk.mehmedbasic.jsonast.JsonIdentifier;
import java.util.Objects;

/**
 * Node name filter.
 */
public class NodeNameFilter extends JsonIdentifierFilter {

  private final String nodeName;

  public NodeNameFilter(String nodeName) {
    this.nodeName = nodeName;
  }

  @Override
  public boolean apply(JsonIdentifier identifier) {
    return Objects.equals(identifier.getName(), nodeName);
  }
}
