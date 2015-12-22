package dk.mehmedbasic.jsonast

/**
 * A json document.
 */
public class JsonDocument extends JsonNodes {

    JsonDocument() {
        super(null)
        document = this
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

}
