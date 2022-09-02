package dk.mehmedbasic.jsonast.selector;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonIdentifier;

/**
 * A filter that works on the identifier of the node.
 */
abstract class JsonIdentifierFilter extends NodeFilter {

  @Override
  public boolean apply(BaseNode node, Integer index) {
    return apply(node.getIdentifier());
  }

  public abstract boolean apply(JsonIdentifier identifier);
}
