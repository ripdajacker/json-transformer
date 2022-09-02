package dk.mehmedbasic.jsonast.transform;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonNodes;

/**
 * Transforms JSON AST according to the implementation.
 */
public abstract class TransformationFunction {

  public abstract void apply(JsonDocument document, JsonNodes root);

  public static void nodeChanged(JsonNodes nodes, BaseNode baseNode) {
    if (nodes instanceof CachingJsonNodes) {
      ((CachingJsonNodes) nodes).nodeChanged(baseNode);
    }
  }
}
