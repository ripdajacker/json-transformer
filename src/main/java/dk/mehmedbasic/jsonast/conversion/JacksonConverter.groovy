package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.*
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.node.*

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
    static JsonDocument asTransformable(JsonNode root) {
        def document = new JsonDocument()
        document.addRoot(convertToTransformable(null, root))
        return document
    }

    static JsonNode asJacksonNode(JsonDocument document) {
        return convertToJackson(document.roots.get(0))
    }

    private static JsonNode convertToJackson(BaseNode baseNode) {
        if (baseNode.isArray()) {
            def result = new ArrayNode(JsonNodeFactory.instance)
            JsonArrayNode arrayNode = baseNode as JsonArrayNode
            for (BaseNode childNode : arrayNode.children) {
                result.add(convertToJackson(childNode))
            }
            return result
        }
        if (baseNode.isValueNode()) {
            JsonValueNode valueNode = baseNode as JsonValueNode
            if (baseNode.isInt()) {
                return new IntNode(valueNode.intValue())
            } else if (baseNode.isDouble()) {
                return new DoubleNode(valueNode.doubleValue())
            } else if (baseNode.isBoolean()) {
                if (valueNode.booleanValue()) {
                    return BooleanNode.TRUE
                } else {
                    return BooleanNode.FALSE
                }
            } else if (baseNode.isString()) {
                return new TextNode(valueNode.stringValue())
            }
        }
        if (baseNode.isObject()) {
            def result = new ObjectNode(JsonNodeFactory.instance)
            def objectNode = baseNode as JsonObjectNode

            for (BaseNode childNode : objectNode.children) {
                def identifier = childNode.identifier
                result.put(identifier.name, convertToJackson(childNode))
            }
            return result
        }
        throw new IllegalArgumentException("Unknown node type: $baseNode")
    }

    /**
     * Converts the given node.
     *
     * @param name the name of the node.
     * @param source the source Jackson node.
     *
     * @return the converted node.
     */
    private static BaseNode convertToTransformable(String name, JsonNode source) {
        if (source.isObject()) {
            def result = new JsonObjectNode()
            result.identifier.name = name
            result.identifier.classes.add("object")

            ObjectNode object = source as ObjectNode
            for (Map.Entry<String, JsonNode> entry : object.fields) {
                result.addChild(convertToTransformable(entry.key, entry.value))
            }
            return result
        } else if (source.isArray()) {
            def result = new JsonArrayNode()
            result.identifier.name = name
            result.identifier.classes.add("array")

            ArrayNode array = source as ArrayNode
            for (JsonNode node : array) {
                result.addChild(convertToTransformable(null, node))
            }
            return result
        } else if (source.isValueNode()) {
            def result = new JsonValueNode()
            result.identifier.name = name
            if (name == "@version") {
                result.identifier.classes << "sysclass_version"
            }

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
