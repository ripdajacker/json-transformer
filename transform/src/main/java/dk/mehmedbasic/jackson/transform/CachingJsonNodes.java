package dk.mehmedbasic.jackson.transform;

import dk.mehmedbasic.css.NodeId;
import dk.mehmedbasic.jackson.BaseNode;
import dk.mehmedbasic.jackson.JsonNodes;
import dk.mehmedbasic.jackson.selector.NodeFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * A throwaway nodes object that handles caching.
 */
public class CachingJsonNodes extends JsonNodes {

  private final Map<String, BaseNode> idToNode = new HashMap<>();
  private final Map<String, List<BaseNode>> nameToNode = new HashMap<>();
  private final Map<String, List<BaseNode>> classToNode = new HashMap<>();

  private boolean dirty = true;

  @Override
  public JsonNodes findByName(String name) {
    checkCache();

    JsonNodes nodes = new JsonNodes();
    nodes.setRoots(new LinkedHashSet<>(getOrCreate(nameToNode, name)));
    return nodes;
  }

  private void checkCache() {
    if (dirty) {
      nameToNode.clear();
      classToNode.clear();
      idToNode.clear();

      for (BaseNode root : getRoots()) {
        collectFiltered(new LinkedHashSet<>(), root, new NodeFilter() {
          @Override
          public boolean apply(BaseNode node, Integer index) {
            registerNode(node);
            return false;
          }
        });
      }

      dirty = false;
    }
  }

  public void nodeChanged(BaseNode node) {
    for (List<BaseNode> list : nameToNode.values()) {
      list.remove(node);
    }

    for (List<BaseNode> list : classToNode.values()) {
      list.remove(node);
    }

    List<String> keysToRemove = new ArrayList<>();
    for (Map.Entry<String, BaseNode> entry : idToNode.entrySet()) {
      if (entry.getValue().equals(node)) {
        keysToRemove.add(entry.getKey());
      }
    }

    keysToRemove.forEach(idToNode::remove);

    registerNode(node);
  }

  /**
   * Registers the node in the cahes.
   *
   * @param node the node to register.
   */
  private void registerNode(final BaseNode node) {
    NodeId identifier = node.getIdentifier();
    if (identifier.getName() != null) {
      getOrCreate(nameToNode, identifier.getName()).add(node);
    }

    identifier.getClasses().forEach(id -> getOrCreate(classToNode, id).add(node));

    if (identifier.getId() != null) {
      idToNode.put(identifier.getId(), node);
    }
  }

  private List<BaseNode> getOrCreate(Map<String, List<BaseNode>> map, String key) {
    map.computeIfAbsent(key, ignored -> new ArrayList<>());
    return map.get(key);
  }
}
