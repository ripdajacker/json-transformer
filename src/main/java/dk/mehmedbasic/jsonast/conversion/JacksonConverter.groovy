package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.*
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.node.ArrayNode
import org.codehaus.jackson.node.ObjectNode

/**
 * Converts jackson nodes to dk.mehmedbasic.jsonast nodes.
 */
class JacksonConverter {
    /**
     * Converts a Jackson node to a JsonDocument.
     *
     * @param root the root to convert.
     *
     * @return the converted document.
     */
    static JsonDocument convert(JsonNode root) {
        def document = new JsonDocument()
        document.addRoot(convertNode(null, root))
        return document
    }

    /**
     * Converts the given node.
     *
     * @param name the name of the node.
     * @param source the source Jackson node.
     *
     * @return the converted node.
     */
    private static BaseNode convertNode(String name, JsonNode source) {
        if (source.isObject()) {
            def result = new JsonObjectNode()
            result.identifier.name = name
            result.identifier.classes.add("object")

            ObjectNode object = source as ObjectNode
            for (Map.Entry<String, JsonNode> entry : object.fields) {
                result.addChild(convertNode(entry.key, entry.value))
            }
            return result
        } else if (source.isArray()) {
            def result = new JsonArrayNode()
            result.identifier.name = name
            result.identifier.classes.add("array")

            ArrayNode array = source as ArrayNode
            for (JsonNode node : array) {
                result.addChild(convertNode(null, node))
            }
            return result
        } else if (source.isValueNode()) {
            def result = new JsonValueNode()
            result.identifier.name = name

            if (source.isBoolean()) {
                result.identifier.classes.add("boolean")
                result.setValue(source.booleanValue)
            } else if (source.isInt()) {
                result.identifier.classes.add("int")
                result.setValue(source.intValue)
            } else if (source.isTextual()) {
                result.identifier.classes.add("string")
                result.setValue(source.textValue)
            } else if (source.isDouble()) {
                result.identifier.classes.add("double")
                result.setValue(source.doubleValue)
            }
            return result
        }
        return null
    }
}
