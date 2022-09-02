package dk.mehmedbasic.jsonast;

import dk.mehmedbasic.jsonast.selector.InFilter;
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine;
import dk.mehmedbasic.jsonast.selector.NodeFilter;
import dk.mehmedbasic.jsonast.selector.NodeNameFilter;
import dk.mehmedbasic.jsonast.transform.CachingJsonNodes;
import dk.mehmedbasic.jsonast.transform.Transformer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A collection of BaseNode objects in a selectable form.
 * <br/><br/>
 * This is a jquery-like object, that contains a list of roots.<br/>
 * When you select within this object, the query runs for all nodes, and the result is a new
 * JsonNodes object with
 * the selected roots.
 */
public class JsonNodes implements Iterable<BaseNode> {

  private Set<BaseNode> roots = new LinkedHashSet<>(5_000, 1f);
  private final Set<BaseNode> exclusions = new LinkedHashSet<>(1000, 1f);
  private JsonDocument document;

  public JsonNodes() {
  }

  private JsonNodes(JsonNodes parent) {
    if (parent != null) {
      document = parent.getDocument();
    }
  }

  /**
   * Selects a subtree given a selector.
   *
   * @param selector the selector to use.
   * @return a subtree containing the selected nodes.
   */
  public JsonNodes select(String selector) {
    if (selector == null || selector.trim().isEmpty()) {
      JsonNodes nodes = new JsonNodes(this);
      nodes.setRoots(new LinkedHashSet<>(roots));
      return nodes;
    }

    JsonSelectionEngine engine;
    try {
      engine = new JsonSelectionEngine(selector);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return engine.execute(this);
  }

  /**
   * Selects the first result given a selector.
   *
   * @param selector the selector to query with.
   * @return an optional BaseNode value.
   */
  public Optional<BaseNode> selectSingle(String selector) {
    JsonNodes result = select(selector);
    if (result.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(result.getRoots().iterator().next());
  }

  /**
   * Finds nodes by name.
   *
   * @param name the name to look for.
   * @return the resulting subtree.
   */
  public JsonNodes findByName(String name) {
    if (name == null) {
      return this;
    } else {
      JsonNodes result = new JsonNodes(this);
      for (BaseNode node : roots) {
        collectFiltered(result.getRoots(), node, createNameFilter(name));
      }

      return result;
    }
  }

  /**
   * Adds a root to the subtree.
   *
   * @param root the root to add.
   */
  public void addRoot(BaseNode root) {
    if (exclusions.contains(root)) {
      return;
    }

    if (roots.contains(root)) {
      return;
    }

    roots.add(root);
  }

  /**
   * Gets the parents for this set of roots, null parents are ignored.
   * <p>
   * The new nodes object may contain fewer roots than this.
   *
   * @return the parents of this.
   */
  public JsonNodes parent() {
    JsonNodes result = new JsonNodes(this);
    for (BaseNode root : roots) {
      if (root.getParent() != null) {
        result.addRoot(root.getParent());
      }
    }
    return result;
  }

  /**
   * Find the first parent of the roots matching the given selector.
   *
   * @param selector the selector.
   * @return the parents.
   */
  public JsonNodes parent(String selector) {
    JsonNodes potentials = document.select(selector);

    JsonNodes result = new JsonNodes(this);
    for (BaseNode root : roots) {
      Optional<BaseNode> relevantParent = findRelevantParent(root, potentials.getRoots());
      relevantParent.ifPresent(result::addRoot);
    }

    return result;
  }

  private static Optional<BaseNode> findRelevantParent(BaseNode root,
      final Set<BaseNode> potentials) {
    return root.parents().stream().filter(potentials::contains)
        .findFirst();
  }

  @Override
  public Iterator<BaseNode> iterator() {
    return roots.iterator();
  }

  /**
   * Filters the subtree given a {@link NodeFilter}
   *
   * @param filter the filter to apply.
   * @return the filtered subtree.
   */
  public JsonNodes filter(NodeFilter filter) {
    JsonNodes result = new JsonNodes(this);

    for (BaseNode node : roots) {
      collectFiltered(result.getRoots(), node, filter.and(createExclusionFilter()));
    }

    return result;
  }

  /**
   * Begins a transformation builder.
   *
   * @param selector the selector.
   * @return the new transformer object.
   */
  public Transformer transform(String selector) {
    return new Transformer(selector, this);
  }

  /**
   * Begins a transformation builder on this JsonNodes instance.
   *
   * @return the transformer object.
   */
  public Transformer transform() {
    return new Transformer(null, this);
  }

  /**
   * Calculates a list of <BaseNode, Integer> pairs, that are sorted by the common ancestor count to
   * this subtree's
   * nodes.
   *
   * @param node the node to calculate the distance to.
   * @return the list of distances.
   */
  public List<NodeDistance> closestTo(BaseNode node) {
    List<NodeDistance> distance = roots.stream().filter(n -> n != node)
        .map(that -> new NodeDistance(that, node.distanceTo(that)))
        .collect(Collectors.toCollection(ArrayList::new));

    if (distance.isEmpty()) {
      return Collections.emptyList();
    } else if (distance.size() == 1) {
      return distance;
    }

    distance.sort(Comparator.comparing(NodeDistance::distance));

    final int shortestDistance = distance.get(0).distance();
    return distance.stream().filter(t -> t.distance() == shortestDistance)
        .collect(Collectors.toList());
  }

  /**
   * The number of roots in this subtree.
   *
   * @return the number of roots.
   */
  public int getRootCount() {
    return roots.size();
  }

  /**
   * The number of roots in this subtree.
   *
   * @return the number of roots.
   */
  public boolean isEmpty() {
    return roots.isEmpty();
  }

  /**
   * Adds an exclusion to the subtree.
   * <br/><br/>
   * The excluded nodes are ignored when added to the subtree.
   *
   * @param node the node to exclude.
   */
  public void addExclusion(BaseNode node) {
    exclusions.add(node);
  }

  /**
   * Recursively traverse the node and collect all nodes that pass the filter into the destination
   * set.
   *
   * @param node the node to traverse.
   */
  protected void collectFiltered(Set<BaseNode> destination, BaseNode node, NodeFilter filter) {
    if (exclusions.contains(node)) {
      return;
    }

    if (node.isArray()) {
      JsonArrayNode arrayNode = (JsonArrayNode) node;

      List<BaseNode> children = arrayNode.getChildren();
      for (int index = 0; index < children.size(); index++) {
        BaseNode child = children.get(index);
        if (filter.apply(child, index)) {
          destination.add(child);
        }

        collectFiltered(destination, child, filter);
      }
    } else if (node.isObject()) {
      JsonObjectNode objectNode = (JsonObjectNode) node;
      for (BaseNode child : objectNode.getChildren()) {
        if (filter.apply(child, null)) {
          destination.add(child);
        }

        collectFiltered(destination, child, filter);
      }
    }
  }

  private NodeFilter createNameFilter(String name) {
    NodeFilter exclusionFilter = createExclusionFilter();
    NodeNameFilter nameFilter = new NodeNameFilter(name);
    return nameFilter.and(exclusionFilter);
  }

  private NodeFilter createExclusionFilter() {
    return new InFilter(exclusions).not();
  }

  /**
   * Returns a new JsonNodes instance that has caching enabled.
   *
   * @return the cached nodes.
   */
  public CachingJsonNodes withCaching() {
    CachingJsonNodes nodes = new CachingJsonNodes();
    nodes.setRoots(new LinkedHashSet<>(roots));
    return nodes;
  }

  public Set<BaseNode> getRoots() {
    return roots;
  }

  public void setRoots(Set<BaseNode> roots) {
    this.roots = roots;
  }

  public Set<BaseNode> getExclusions() {
    return exclusions;
  }

  public JsonDocument getDocument() {
    return document;
  }

  public void setDocument(JsonDocument document) {
    this.document = document;
  }
}
