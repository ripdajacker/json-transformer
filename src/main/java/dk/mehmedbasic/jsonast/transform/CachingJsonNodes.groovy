package dk.mehmedbasic.jsonast.transform

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Multimap
import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonNodes

/**
 * A throwaway nodes object that handles caching.
 */
class CachingJsonNodes extends JsonNodes {
    private Map<String, BaseNode> idToNode = new HashMap<>()
    private Multimap<String, BaseNode> nameToNode = LinkedListMultimap.create()
    private Multimap<String, BaseNode> classToNode = LinkedListMultimap.create()

    private boolean dirty = true

    CachingJsonNodes() {
    }

    @Override
    JsonNodes findByName(String name) {
        if (dirty) {
            nameToNode.clear()
            classToNode.clear()
            idToNode.clear()

            for (BaseNode root : roots) {
                collectFiltered(roots, root, { BaseNode node, Integer index -> registerNode(node) })
            }
            dirty = false
        }

        def nodes = new JsonNodes()
        nodes.roots = new LinkedHashSet<>(nameToNode.get(name))
        return nodes
    }

    void nodeChanged(BaseNode node) {
        def nameKeys = new LinkedHashSet(nameToNode.keySet())
        for (String key : nameKeys) {
            nameToNode.get(key).remove(node)
        }

        def classKeys = new LinkedHashSet(classToNode.keySet())
        for (String key : classKeys) {
            classToNode.get(key).remove(node)
        }

        List<String> keysToRemove = []
        for (Map.Entry<String, BaseNode> entry : idToNode.entrySet()) {
            if (entry.value == node) {
                keysToRemove << entry.key
            }
        }
        keysToRemove.each { idToNode.remove(it) }

        registerNode(node)
    }

    /**
     * Registers the node in the cahes.
     *
     * @param node the node to register.
     */
    private void registerNode(BaseNode node) {
        def identifier = node.identifier
        if (identifier.name != null) {
            nameToNode.get(identifier.name).add(node)
        }

        identifier.classes.each {
            if (it != null) {
                classToNode.get(it).add(node)
            }
        }

        if (identifier.id != null) {
            idToNode[identifier.id] = node
        }
    }

}
