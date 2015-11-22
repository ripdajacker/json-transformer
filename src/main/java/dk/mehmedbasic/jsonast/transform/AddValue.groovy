package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.*
import groovy.transform.PackageScope

/**
 * TODO[JEKM] - someone remind me to document this class.
 */
@PackageScope
final class AddValue implements TransformStrategy {
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
        def newChild = createNode(document)
        newChild.identifier.name = name
        if (value) {
            if (newChild.array) {
                newChild.addChild(new JsonValueNode(value))
            } else if (newChild.valueNode) {
                newChild.setValue(value)
            }
        }

        for (BaseNode node : root.roots) {
            node.addChild(newChild)

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
        }
        return null
    }
}
