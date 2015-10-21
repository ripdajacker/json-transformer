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

        for (String className : node.identifier.classes) {
            classesToNode.get(className).add(node)
        }
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


    void treeChanged() {
        this.dirty = true
    }
}
