package dk.mehmedbasic.jackson.transform;

import dk.mehmedbasic.jackson.BaseNode;
import dk.mehmedbasic.jackson.JsonDocument;
import dk.mehmedbasic.jackson.JsonNodes;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Moves nodes from one place to another.
 */
final class Mover extends TransformationFunction {

  private final String selector;

  public Mover(String selector) {
    this.selector = selector;
  }

  @Override
  public void apply(JsonDocument document, final JsonNodes root) {
    CachingJsonNodes queryRoot = document.select(null).withCaching();

    Map<BaseNode, BaseNode> changes = new LinkedHashMap<>();
    for (BaseNode source : root.getRoots()) {
      queryRoot.getExclusions().clear();
      queryRoot.getExclusions().add(source);

      JsonNodes newDestinations = queryRoot.select(selector);

      BaseNode destination = null;

      if (newDestinations.getRootCount() > 1) {
        List<NodeDistance> closest = newDestinations.closestTo(source);
        if (closest.size() == 1) {
          destination = closest.get(0).node();
        }
      } else if (!newDestinations.isEmpty()) {
        destination = newDestinations.getRoots().iterator().next();
      }

      if (destination != null) {
        changes.put(source, destination);
      }
    }

    for (Map.Entry<BaseNode, BaseNode> entry : changes.entrySet()) {
      BaseNode source = entry.getKey();
      BaseNode destination = entry.getValue();

      source.changeParent(destination);
    }
  }
}
