package dk.mehmedbasic.jsonast

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import com.google.common.collect.*
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import dk.mehmedbasic.jsonast.selector.NodeFilter
import dk.mehmedbasic.jsonast.transform.Transformer
import groovy.transform.CompileStatic

/**
 * A collection of BaseNode objects in a selectable form.
 * <br/><br/>
 * This is a jquery-like object, that contains a list of roots.<br/>
 * When you select within this object, the query runs for all roots.
 */
@CompileStatic
class JsonNodes implements Iterable<BaseNode> {
    protected Map<String, BaseNode> _idToNode = new TreeMap<>()

    protected Multimap<String, BaseNode> _nameToNode = LinkedHashMultimap.create(10_0000, 100)
    protected Multimap<String, BaseNode> _classesToNode = LinkedHashMultimap.create(10_0000, 100)

    protected Set<BaseNode> _nodes = new LinkedHashSet<>(10_000, 1f)
    protected Set<BaseNode> _roots = new LinkedHashSet<>(5_000, 1f)

    Set<BaseNode> exclusions = new LinkedHashSet<>(1000, 1f)

    private boolean dirty = false
    JsonDocument document

    private Predicate<BaseNode> nodesFilter = Predicates.alwaysTrue()
    private Predicate<BaseNode> rootsFilter = Predicates.alwaysTrue()


    private Map<String, BaseNode> cachedIdToNode
    private Multimap<String, BaseNode> cachedNameToNode
    private Multimap<String, BaseNode> cachedClassesTooNode

    private Set<BaseNode> cachedRoots
    private Set<BaseNode> cachedNodes

    int _rootCount = -1

    JsonNodes(JsonNodes parent) {
        if (parent != null) {
            document = parent.document

            _classesToNode = parent._classesToNode
            _idToNode = parent._idToNode
            _nameToNode = parent._nameToNode

            _roots = parent.nodes
            _nodes = parent.nodes
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

            result._roots = Sets.newLinkedHashSet(named)

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
        if (_roots.contains(root)) {
            return
        }
        _roots.add(root)
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
        this._nodes.add(node)
    }

    /**
     * Registers a node in this subtree.
     *
     * @param node the node to register.
     */
    protected void register(BaseNode node) {
        if (node.identifier.id != null) {
            _idToNode.put(node.identifier.id, node)
        }
        if (node.identifier.name != null) {
            _nameToNode.get(node.identifier.name).add(node)
        }

        for (String className : node.identifier.classes) {
            _classesToNode.get(className).add(node)
        }
    }

    /**
     * Gets the parents for this set of roots.
     *
     * @return the parents of this.
     */
    JsonNodes parent() {
        def result = new JsonNodes(document)
        // TODO
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
        def nodes = new JsonNodes(this)

        def filterPredicate = new Predicate<BaseNode>() {
            @Override
            boolean apply(BaseNode input) {
                return filter.apply(input)
            }
        }


        def descendantOfThis = new Predicate<BaseNode>() {
            @Override
            boolean apply(BaseNode input) {
                for (BaseNode node : nodes.roots) {
                    if (input.parents().contains(node)) {
                        return true

                    }
                }
                return false
            }
        }

        nodes.rootsFilter = filterPredicate
        nodes.nodesFilter = descendantOfThis

        return nodes
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
        return _rootCount
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
            cachedClassesTooNode = null
            cachedIdToNode = null
            cachedNameToNode = null
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
        if (cachedRoots == null) {
            cachedRoots = Sets.filter(_roots, rootsFilter)
        }
        return cachedRoots

    }

    Set<BaseNode> getNodes() {
        if (cachedNodes == null) {
            cachedNodes = Sets.filter(_nodes, nodesFilter)
        }
        return cachedNodes
    }

    Map<String, BaseNode> getIdToNode() {
        if (cachedIdToNode == null) {
            cachedIdToNode = Maps.filterValues(_idToNode, nodesFilter)
        }
        return cachedIdToNode
    }

    Multimap<String, BaseNode> getNameToNode() {
        if (cachedNameToNode == null) {
            cachedNameToNode = Multimaps.filterValues(_nameToNode, nodesFilter)
        }
        return _nameToNode
    }


}
