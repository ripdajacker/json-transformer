package dk.mehmedbasic.jsonast

import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import org.codehaus.jackson.map.ObjectMapper

/**
 * A json document.
 */
public class JsonDocument extends JsonNodes implements NodeChangedListener {
    protected Set<BaseNode> nodes = new LinkedHashSet<>()

    JsonDocument() {
        super(null)
        document = this
    }

    @Override
    void addNode(BaseNode node) {
        super.addNode(node)
        node.listener = this
        nodes.add(node)
    }

    JsonValueNode createValueNode() {
        def node = new JsonValueNode()
        addNode(node)
        return node
    }

    JsonArrayNode createArrayNode() {
        def node = new JsonArrayNode()
        addNode(node)
        return node
    }

    JsonObjectNode createObjectNode() {
        def node = new JsonObjectNode()
        addNode(node)
        return node
    }

    static JsonDocument parse(InputStream inputStream) {
        def mapper = new ObjectMapper()
        def tree = mapper.readTree(inputStream)
        return JacksonConverter.asTransformable(tree)
    }

    static JsonDocument parse(String content) {
        def mapper = new ObjectMapper()
        def tree = mapper.readTree(content)
        return JacksonConverter.asTransformable(tree)
    }

    @Override
    void nodeChanged(NodeChangeEventType type, BaseNode node) {
        if (type == NodeChangeEventType.NodeDeleted) {
            privateRemove(node)
        } else if (type == NodeChangeEventType.NodeAdded) {
            privateAdd(node)
        } else if (type == NodeChangeEventType.NodeChanged) {
            privateRemove(node)
            privateAdd(node)

            node.cleanDirtyState()
        }
    }

    private void privateAdd(BaseNode node) {
        register(node)
    }

    private void privateRemove(BaseNode node) {
        def id = node.identifier.id
        if (id != null) {
            _idToNode.remove(id)
        }
        def name = node.identifier.name
        if (name != null) {
            _nameToNode.get(name).remove(node)
        }

        for (String className : node.identifier.classes) {
            _classesToNode.get(className).remove(node)
        }

        nodes.remove(node)
    }
}
