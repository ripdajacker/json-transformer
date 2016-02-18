package dk.mehmedbasic.jsonast

import dk.mehmedbasic.jsonast.selector.InFilter
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import dk.mehmedbasic.jsonast.selector.NodeFilter
import dk.mehmedbasic.jsonast.selector.NodeNameFilter
import dk.mehmedbasic.jsonast.transform.CachingJsonNodes
import dk.mehmedbasic.jsonast.transform.Transformer
import groovy.transform.CompileStatic

/**
 * A collection of BaseNode objects in a selectable form.
 * <br/><br/>
 * This is a jquery-like object, that contains a list of roots.<br/>
 * When you select within this object, the query runs for all nodes, and the result is a new JsonNodes object with
 * the selected roots.
 */
@CompileStatic
class JsonNodes implements Iterable<BaseNode> {
    Set<BaseNode> roots = new LinkedHashSet<>(5_000, 1f)
    Set<BaseNode> exclusions = new LinkedHashSet<>(1000, 1f)

    JsonDocument document

    JsonNodes() {
    }

    private JsonNodes(JsonNodes parent) {
        if (parent != null) {
            document = parent.document
        }
    }

    /**
     * Selects a subtree given a selector.
     *
     * @param selector the selector to use.
     *
     * @return a subtree containing the selected nodes.
     */
    JsonNodes select(String selector) {
        if (selector == null || selector.trim().empty) {
            def nodes = new JsonNodes(this)
            nodes.roots = new LinkedHashSet<>(roots)
            return nodes;
        }
        def engine = new JsonSelectionEngine(selector)
        engine.execute(this)
    }

    /**
     * Selects the first result given a selector.
     *
     * @param selector the selector to query with.
     * @return an optional BaseNode value.
     */
    Optional<BaseNode> selectSingle(String selector) {
        def result = select(selector)
        if (result.empty) {
            return Optional.empty()
        }
        return Optional.of(result.roots[0])
    }

    /**
     * Finds nodes by name.
     *
     * @param name the name to look for.
     *
     * @return the resulting subtree.
     */
    JsonNodes findByName(String name) {
        if (name == null) {
            return this
        } else {
            def result = new JsonNodes(this)
            for (BaseNode node : roots) {
                collectFiltered(result.roots, node, createNameFilter(name))
            }
            return result
        }

    }

    /**
     * Adds a root to the subtree.
     *
     * @param root the root to add.
     */
    void addRoot(BaseNode root) {
        if (exclusions.contains(root)) {
            return
        }
        if (roots.contains(root)) {
            return
        }
        roots.add(root)
    }

    /**
     * Gets the parents for this set of roots.
     *
     * @return the parents of this.
     */
    JsonNodes parent() {
        // TODO
        def result = new JsonNodes(document)
        for (BaseNode root : roots) {
            if (root.parent) {
                result.addRoot(root.parent)
            }
        }
        return result
    }

    /**
     * Find the first parent of the roots matching the given selector.
     *
     * @param selector the selector.
     *
     * @return the parents.
     */
    JsonNodes parent(String selector) {
        // TODO
        def potentials = document.select(selector)

        def result = new JsonNodes(document)
        for (BaseNode root : roots) {
            def relevantParent = findRelevantParent(root, potentials.roots)
            if (relevantParent) {
                result.addRoot(relevantParent)
            }
        }
        return result
    }

    private static BaseNode findRelevantParent(BaseNode root, Set<BaseNode> potentials) {
        root.parents().find {
            potentials.contains(it)
        }
    }

    @Override
    Iterator<BaseNode> iterator() {
        return roots.iterator()
    }

    /**
     * Filters the subtree given a {@link NodeFilter}
     *
     * @param filter the filter to apply.
     *
     * @return the filtered subtree.
     */
    JsonNodes filter(NodeFilter filter) {
        def result = new JsonNodes(this)

        for (BaseNode node : roots) {
            collectFiltered(result.roots, node, filter.and(createExclusionFilter()))
        }

        return result
    }

    /**
     * Begins a transformation builder.
     *
     * @param selector the selector.
     *
     * @return the new transformer object.
     */
    Transformer transform(String selector) { return new Transformer(selector, this) }

    /**
     * Begins a transformation builder on this JsonNodes instance.
     *
     * @return the transformer object.
     */
    Transformer transform() { return new Transformer(null, this) }

    /**
     * Calculates a list of <BaseNode, Integer> pairs, that are sorted by the common ancestor count to this subtree's
     * nodes.
     *
     * @param node the node to calculate the distance to.
     *
     * @return the list of distances.
     */
    List<Tuple2<BaseNode, Integer>> closestTo(BaseNode node) {
        List<Tuple2<BaseNode, Integer>> distance = []
        for (BaseNode that : roots.findAll { it != node }) {
            distance << new Tuple2<BaseNode, Integer>(that, node.editDistance(that))
        }
        Collections.sort(distance, new Comparator<Tuple2<BaseNode, Integer>>() {
            @Override
            int compare(Tuple2<BaseNode, Integer> o1, Tuple2<BaseNode, Integer> o2) {
                return o1.second.compareTo(o2.second)
            }
        })
        if (distance.isEmpty()) {
            return []
        }

        if (distance.size() == 1) {
            return [distance.first()]
        }

        int shortest = distance.first().second
        return distance.findAll { it.second == shortest }
    }

    /**
     * The number of roots in this subtree.
     *
     * @return the number of roots.
     */
    int getLength() {
        roots.size()
    }
    /**
     * The number of roots in this subtree.
     *
     * @return the number of roots.
     */
    boolean isEmpty() {
        return roots.empty
    }

    /**
     * Adds an exclusion to the subtree.
     * <br/><br/>
     * The excluded nodes are ignored when added to the subtree.
     *
     * @param node the node to exclude.
     */
    void addExclusion(BaseNode node) {
        exclusions << node
    }

    /**
     * Recursively traverse the node and collect all nodes that pass the filter into the destination set.
     *
     * @param node the node to traverse.
     */
    protected void collectFiltered(Set<BaseNode> destination, BaseNode node, NodeFilter filter) {
        if (exclusions.contains(node)) {
            return
        }
        if (node.array) {
            def arrayNode = node as JsonArrayNode
            int index = 0
            for (BaseNode child : arrayNode.children) {
                if (filter.apply(child, index)) {
                    destination << child
                }
                index++
                collectFiltered(destination, child, filter)
            }
        } else if (node.object) {
            def objectNode = node as JsonObjectNode
            for (BaseNode child : objectNode.children) {
                if (filter.apply(child, null)) {
                    destination << child
                }
                collectFiltered(destination, child, filter)
            }
        }
    }


    private NodeFilter createNameFilter(String name) {
        NodeFilter exclusionFilter = createExclusionFilter()
        def nameFilter = new NodeNameFilter(name)
        def nodeFilter = nameFilter.and(exclusionFilter)
        nodeFilter
    }

    private NodeFilter createExclusionFilter() {
        new InFilter(exclusions).not()
    }

    /**
     * Returns a new JsonNodes instance that has caching enabled.
     *
     * @return the cached nodes.
     */
    CachingJsonNodes withCaching() {
        def nodes = new CachingJsonNodes()
        nodes.roots = new LinkedHashSet<>(roots)
        return nodes
    }

}
