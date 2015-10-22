package dk.mehmedbasic.jsonast

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Multimap
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import dk.mehmedbasic.jsonast.selector.NodeFilter
import groovy.transform.TypeChecked

/**
 * A collection of BaseNode objects in a selectable form.
 * <br/><br/>
 * This is a jquery-like object, that contains a list of roots.<br/>
 * When you select within this object, the query runs for all roots.
 */
@TypeChecked
class JsonNodes implements Iterable<BaseNode> {
    private Map<String, BaseNode> idToNode = [:]
    private Multimap<String, BaseNode> nameToNode = LinkedListMultimap.create()
    private Multimap<String, BaseNode> classesToNode = LinkedListMultimap.create()

    private List<BaseNode> nodes = new LinkedList<>()
    List<BaseNode> roots = new LinkedList<>()

    private List<BaseNode> exclusions = new LinkedList<>()

    private boolean dirty = false

    /**
     * Selects a subtree given a selector.
     *
     * @param selector the selector to use.
     *
     * @return a subtree containing the selected nodes.
     */
    JsonNodes select(String selector) {
        checkDirtyState()
        if (selector == null || selector.trim().isEmpty()) {
            return this;
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
        checkDirtyState()
        def result = select(selector)
        if (result.length == 0) {
            return Optional.empty()
        }
        return Optional.of(result.roots[0])
    }

    /**
     * Finds a node by id.
     *
     * @param id the id of the node.
     * @return an optional BaseNode value.
     */
    Optional<BaseNode> findById(String id) {
        checkDirtyState()
        return Optional.ofNullable(idToNode.get(id))
    }

    /**
     * Finds nodes by name.
     *
     * @param name the name to look for.
     *
     * @return the resulting subtree.
     */
    JsonNodes findByName(String name) {
        checkDirtyState()
        def result = new JsonNodes()
        if (name == null) {
            for (BaseNode root : roots) {
                result.addRoot(root)
            }
        } else {
            for (BaseNode node : nameToNode.get(name)) {
                result.addRoot(node)
            }
        }

        return result
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
        recursivelyAdd(root)
    }

    /**
     * Recursively add the given node and its entire subtree.
     *
     * @param node the node to traverse.
     */
    private void recursivelyAdd(BaseNode node) {
        if (exclusions.contains(node)) {
            return
        }
        addNode(node)
        if (node.isArray()) {
            def arrayNode = node as JsonArrayNode
            for (BaseNode child : arrayNode.children) {
                recursivelyAdd(child)
            }
        } else if (node.isObject()) {
            def objectNode = node as JsonObjectNode
            for (BaseNode child : objectNode.children) {
                recursivelyAdd(child)
            }
        }

    }

    /**
     * Adds a node to this tree.
     *
     * @param node the node to add.
     */
    void addNode(BaseNode node) {
        register(node)
        this.nodes.add(node)
    }

    /**
     * Registers a node in this subtree.
     *
     * @param node the node to register.
     */
    private void register(BaseNode node) {
        idToNode.put(node.identifier.id, node)
        nameToNode.get(node.identifier.name).add(node)

        for (String className : node.identifier.classes) {
            classesToNode.get(className).add(node)
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
        def result = new JsonNodes()
        for (BaseNode node : nodes) {
            if (filter.apply(node)) {
                result.addRoot(node)
            }
        }
        return result
    }

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
        for (BaseNode that : this.nodes.findAll { it != node }) {
            distance << new Tuple2<BaseNode, Integer>(that, node.commonAncestor(that))
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
     * Checks the dirty state of the subtree.
     */
    private void checkDirtyState() {
        if (dirty) {
            idToNode.clear()
            nameToNode.clear()
            classesToNode.clear()


            List<BaseNode> copy = new ArrayList<>(nodes)
            nodes.clear()
            for (BaseNode node : copy) {
                if (node.parent != null) {
                    node.cleanDirtyState()
                    addNode(node)
                }
            }
            dirty = false
        }
    }

    /**
     * Called when the tree is changed.
     */
    void treeChanged() {
        this.dirty = true
    }
}
