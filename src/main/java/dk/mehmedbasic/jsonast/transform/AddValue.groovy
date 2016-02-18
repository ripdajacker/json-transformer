package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.*
import groovy.transform.PackageScope

/**
 * Adds a value
 */
@PackageScope
final class AddValue extends TransformStrategy {
    private JsonType type = JsonType.Value
    private Object value
    private String name

    AddValue(String name, JsonType type, Object value) {
        this.name = name
        if (type) {
            this.type = type
        }
        this.value = value
    }

    @Override
    void apply(JsonDocument document, JsonNodes root) {
        BaseNode newChild
        if (value instanceof BaseNode) {
            // Verbatim addition
            newChild = value as BaseNode
            newChild.identifier.name = name
        } else {
            newChild = createNode(document)
            newChild.identifier.name = name
            if (value) {
                if (newChild.array) {
                    newChild.addChild(new JsonValueNode(value))
                } else if (newChild.valueNode) {
                    newChild.setValue(value)
                }
            }
        }


        def roots = new LinkedHashSet<BaseNode>(root.roots)
        for (BaseNode node : roots) {
            node.addChild(newChild)

            nodeChanged(root, node)
            nodeChanged(root, newChild)
        }
    }

    private BaseNode createNode(JsonDocument document) {
        switch (type) {
            case JsonType.Array:
                return document.createArrayNode()
            case JsonType.Object:
                return document.createObjectNode()
            case JsonType.Value:
                return document.createValueNode()
            default:
                return null
        }
    }
}
