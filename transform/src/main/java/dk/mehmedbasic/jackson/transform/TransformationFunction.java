package dk.mehmedbasic.jackson.transform;

import com.fasterxml.jackson.databind.JsonNode;
import dk.mehmedbasic.tree.BaseNode;
import dk.mehmedbasic.tree.NodeList;

/**
 * Transforms JSON AST according to the implementation.
 */
public abstract class TransformationFunction {

  public abstract void apply(NodeList<JsonNode> roots);

  public static void nodeChanged(NodeList<JsonNode> nodes, BaseNode<JsonNode> baseNode) {
    // if (nodes instanceof CachingJsonNodes) {
    //   ((CachingJsonNodes) nodes).nodeChanged(baseNode);
    // }
  }
}
