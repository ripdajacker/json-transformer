package dk.mehmedbasic.jsonast

/**
 * The Json type enum.
 */
enum JsonType {
    Value, Array, Object

    static JsonType fromNode(BaseNode node) {
        if (node.array) {
            return Array
        }
        if (node.object) {
            return Object
        }
        if (node.valueNode) {
            return Value
        }
        return null
    }
}