package dk.mehmedbasic.jsonast

import com.google.common.base.Predicate
import com.google.common.collect.*
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import dk.mehmedbasic.jsonast.selector.NodeFilter
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
    Map<String, BaseNode> idToNode = new TreeMap<>()

    Multimap<String, BaseNode> nameToNode = LinkedHashMultimap.create(10_0000, 100)
    Multimap<String, BaseNode> classesToNode = LinkedHashMultimap.create(10_0000, 100)

    Set<BaseNode> nodes = new LinkedHashSet<>(10_000, 1f)
    Set<BaseNode> roots = new LinkedHashSet<>(5_000, 1f)

    Set<BaseNode> exclusions = new LinkedHashSet<>(1000, 1f)

    private boolean dirty = false
    JsonDocument document

    int _rootCount = -1

    JsonNodes(JsonNodes parent) {
        if (parent != null) {
            document = parent.document

            classesToNode = parent.classesToNode
            idToNode = parent.idToNode
            nameToNode = parent.nameToNode

            roots = parent.nodes
            nodes = parent.nodes
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
        if (result.isEmpty()) {
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
        if (name == null) {
            return this
        } else {
            def result = new JsonNodes(this)
            def named = nameToNode.get(name)

            result.roots = Sets.newLinkedHashSet(named)

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
        _rootCount++
        recursivelyAdd(root)
    }

    /**
     * Recursively add the given node and its entire subtree.
     *
     * @param node the node to traverse.
     */
    void recursivelyAdd(BaseNode node) {
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
        nodes.add(node)
    }

    /**
     * Registers a node in this subtree.
     *
     * @param node the node to register.
     */
    protected void register(BaseNode node) {
        if (node.identifier.id != null) {
            idToNode.put(node.identifier.id, node)
        }
        if (node.identifier.name != null) {
            nameToNode.get(node.identifier.name).add(node)
        }

        for (String className : node.identifier.classes) {
            classesToNode.get(className).add(node)
        }
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

        def rootFilter = new Predicate<BaseNode>() {
            @Override
            boolean apply(BaseNode input) {
                return filter.apply(input)
            }
        }


        def descendantOfThis = new Predicate<BaseNode>() {
            @Override
            boolean apply(BaseNode input) {
                for (BaseNode node : result.roots) {
                    if (input.parents().contains(node)) {
                        return true

                    }
                }
                return false
            }
        }

        result.roots = Sets.filter(this.nodes, rootFilter)
        result.nodes = Sets.filter(this.nodes, descendantOfThis)

        result.idToNode = Maps.filterValues(idToNode, descendantOfThis)
        result.nameToNode = Multimaps.filterValues(nameToNode, descendantOfThis)
        result.classesToNode = Multimaps.filterValues(classesToNode, descendantOfThis)

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

    private int getRootCount() {
        if (_rootCount == -1) {
            _rootCount = Iterables.size(getRoots())
        }
        return Iterables.size(getRoots())
    }
    /**
     * The number of roots in this subtree.
     *
     * @return the number of roots.
     */
    int getLength() {
        rootCount
    }
    /**
     * The number of roots in this subtree.
     *
     * @return the number of roots.
     */
    boolean isEmpty() {
        return rootCount == 0
    }

    /**
     * A optimization feature.
     *
     * @return if there is more than one root
     */
    boolean hasMoreThanOneRoot() {
        return rootCount > 1
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
            _rootCount = -1
            dirty = false
        }
    }

    /**
     * Called when the tree is changed.
     */
    void treeChanged() {
        this.dirty = true
    }

    Set<BaseNode> getRoots() {
        return roots
    }

    Map<String, BaseNode> getIdToNode() {
        return idToNode
    }

    Multimap<String, BaseNode> getNameToNode() {
        return nameToNode
    }
}
