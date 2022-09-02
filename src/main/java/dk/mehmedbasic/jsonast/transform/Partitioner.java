package dk.mehmedbasic.jsonast.transform;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonNodes;
import dk.mehmedbasic.jsonast.JsonObjectNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Partitions a node given a list of List<String>.
 */
public final class Partitioner extends TransformationFunction {

  private final Map<String, List<String>> partitionKeys = new LinkedHashMap<>();

  public Partitioner(List<List<String>> partitionKeys) {
    for (List<String> keys : partitionKeys) {
      if (keys.size() >= 2) {
        this.partitionKeys.put(keys.get(0), keys.subList(1, keys.size()));
      }
    }
  }

  @Override
  public void apply(JsonDocument document, JsonNodes root) {
    List<BaseNode> nodes = new ArrayList<>(root.getRoots());
    for (BaseNode source : nodes) {
      for (Map.Entry<String, List<String>> entry : partitionKeys.entrySet()) {
        if (source.isObject() && source.getParent() != null) {
          JsonObjectNode destination = JsonDocument.createObjectNode();

          String newKey = entry.getKey();
          destination.getIdentifier().setName(newKey);
          destination.getIdentifier().addClass("sysclass_partitioned");

          source.getParent().addChild(destination);

          for (String key : entry.getValue()) {
            BaseNode node = source.get(key);
            if (node == null) {
              throw new IllegalArgumentException("The given node was not found " + key);
            }

            source.removeNode(node);

            destination.addChild(node);

            nodeChanged(root, source);
            nodeChanged(root, destination);
            nodeChanged(root, node);
          }
        }
      }
    }
  }
}
