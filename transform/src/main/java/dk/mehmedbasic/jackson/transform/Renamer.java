package dk.mehmedbasic.jackson.transform;

import com.fasterxml.jackson.databind.JsonNode;
import dk.mehmedbasic.tree.BaseNode;
import dk.mehmedbasic.tree.NodeList;

/**
 * Renames nodes or their children.
 */
final class Renamer extends TransformationFunction {

  private final String to;
  private final String from;

  public Renamer(String from, String to) {
    this.from = from;
    this.to = to;
  }

  public Renamer(String to) {
    this(null, to);
  }

  @Override
  public void apply(NodeList<JsonNode> roots) {
    for (BaseNode<JsonNode> node : roots.getNodes()) {
      if (from != null && from.length() > 0) {
        var fromNode = node.get(from);
        if (fromNode != null) {
          fromNode.getIdentifier().setName(to);
        }
        nodeChanged(roots, node);
      } else {
        node.getIdentifier().setName(to);
        nodeChanged(roots, node);
      }
    }
  }
}
