package dk.mehmedbasic.jsonast

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Multimap
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import dk.mehmedbasic.jsonast.selector.NodeFilter
import groovy.transform.TypeChecked

/**
 * A collection of BaseNode objects in a selectable form.
 *
 * This is S
 */
@TypeChecked
class JsonNodes implements Iterable<BaseNode> {
    private Map<String, BaseNode> idToNode = [:]
    private Multimap<String, BaseNode> nameToNode = LinkedListMultimap.create()
    private Multimap<String, BaseNode> tagToNode = LinkedListMultimap.create()

    private List<BaseNode> nodes = new LinkedList<>()
    List<BaseNode> roots = new LinkedList<>()

    private List<BaseNode> exclusions = new LinkedList<>()

    private boolean dirty = false

    JsonNodes select(String selector) {
        checkDirtyState()
        if (selector == null || selector.trim().isEmpty()) {
            return this;
        }
        def engine = new JsonSelectionEngine(selector)
        engine.execute(this)
    }

    Optional<BaseNode> selectSingle(String selector) {
        checkDirtyState()
        def result = select(selector)
        if (result.length == 0) {
            return Optional.empty()
        }
        return Optional.of(result.roots[0])
    }

    Optional<BaseNode> findById(String id) {
        checkDirtyState()
        return Optional.ofNullable(idToNode.get(id))
    }

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

    void addRoot(BaseNode node) {
        if (exclusions.contains(node)) {
            return
        }
        if (roots.contains(node)) {
            return
        }
        roots.add(node)
        recursivelyAdd(node)
    }

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

    boolean addNode(BaseNode baseNode) {
        register(baseNode)
        this.nodes.add(baseNode)
    }


    private void register(BaseNode node) {
        idToNode.put(node.identifier.id, node)
        nameToNode.get(node.identifier.name).add(node)
        tagToNode.get(node.identifier.tag).add(node)
    }

    @Override
    Iterator<BaseNode> iterator() {
        return roots.iterator()
    }

    JsonNodes filter(NodeFilter filter) {
        def result = new JsonNodes()
        for (BaseNode node : nodes) {
            if (filter.apply(node)) {
                result.addRoot(node)
            }
        }
        return result
    }

    int getLength() {
        roots.size()
    }

    void addExclusion(BaseNode node) {
        exclusions << node
    }

    private void checkDirtyState() {
        if (dirty) {
            idToNode.clear()
            nameToNode.clear()
            tagToNode.clear()


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


    void treeChanged() {
        this.dirty = true
    }
}
