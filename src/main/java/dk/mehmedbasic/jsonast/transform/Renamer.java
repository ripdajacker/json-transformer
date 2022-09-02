package dk.mehmedbasic.jsonast.transform;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonNodes;

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
  public void apply(JsonDocument document, JsonNodes root) {
    for (BaseNode node : root.getRoots()) {
      if (from != null && from.length() > 0) {
        node.renameNode(from, to);
        nodeChanged(root, node);
      } else {
        if (node.getParent() != null) {
          node.getParent().renameNode(node, to);
        }

        nodeChanged(root, node);
      }
    }
  }
}
