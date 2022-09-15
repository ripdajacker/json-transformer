package dk.mehmedbasic.jackson.transform;

import dk.mehmedbasic.jackson.BaseNode;
import dk.mehmedbasic.jackson.JsonArrayNode;
import dk.mehmedbasic.jackson.JsonBaseNode;
import dk.mehmedbasic.jackson.JsonDocument;
import dk.mehmedbasic.jackson.JsonNodes;
import dk.mehmedbasic.jackson.JsonValueNode;
import java.util.List;

/**
 * A merger manipulation. Merges two values into one
 */
final class Merger extends TransformationFunction {

  private final String selector;
  private final MergeValueFunction function;

  public Merger(String selector, MergeValueFunction function) {
    this.selector = selector;
    this.function = function;
  }

  public void apply(JsonDocument document, final JsonNodes root) {
    CachingJsonNodes queryRoot = document.select(null).withCaching();

    for (BaseNode source : root.getRoots()) {
      queryRoot.getExclusions().clear();
      queryRoot.addExclusion(source);

      JsonNodes newDestinations = queryRoot.select(selector);

      BaseNode destination = null;
      if (newDestinations.getRootCount() > 1) {
        List<NodeDistance> closest = newDestinations.closestTo(source);
        if (closest.size() == 1) {
          destination = closest.get(0).node();
        }
      } else if (newDestinations.getRootCount() == 1) {
        destination = newDestinations.getRoots().iterator().next();
      }

      if (destination != null) {
        if (destination.isObject()) {
          // Destination is object, add the source
          destination.addChild(source);
        } else if (destination.isArray()) {
          // Destination is array, add the source and wipe the name
          source.getIdentifier().setName(null);
          destination.addChild(source);
        } else if (destination.isValueNode()) {
          JsonValueNode valueNode = (JsonValueNode) destination;
          // Destination is JsonValue, apply the given function
          if (function != null) {
            if (source.isArray()) {
              function.applyArray((JsonArrayNode) source, valueNode);
            } else if (source.isObject()) {
              function.applyObject((JsonBaseNode) source, valueNode);
            } else if (source.isValueNode()) {
              function.applyValue((JsonValueNode) source, valueNode);
            }

            queryRoot.nodeChanged(source);
            queryRoot.nodeChanged(destination);
          }
        }
      }
    }
  }
}