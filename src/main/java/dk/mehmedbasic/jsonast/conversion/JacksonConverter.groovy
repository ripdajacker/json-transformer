package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.*
import groovy.transform.TypeChecked
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.node.*

/**
 * Converts jackson nodes to dk.mehmedbasic.jsonast nodes.
 */
@TypeChecked
class JacksonConverter {

    /**
     * Converts a Jackson node to a JsonDocument.
     *
     * @param root the root to convert.
     * @param namingStrategy the naming strategy.
     *
     * @return the converted document.
     */
    static JsonDocument asTransformable(JsonNode root, ConversionNamingStrategy namingStrategy = null) {
        return new Converter(namingStrategy).asTransformable(root)
    }

    /**
     * Converts the transformable into a Jackson tree.
     *
     * @param document the document
     * @param namingStrategy the naming strategy.
     *
     * @return the resulting node.
     */
    static JsonNode asJacksonNode(JsonDocument document, ConversionNamingStrategy namingStrategy = null) {
        return new Converter(namingStrategy).asJacksonNode(document)
    }


    private static final class Converter {
        ConversionNamingStrategy strategy

        Converter(ConversionNamingStrategy strategy) {
            this.strategy = strategy
            if (strategy == null) {
                this.strategy = new DefaultNamingStrategy()
            }
        }

        JsonDocument asTransformable(JsonNode root) {
            def document = new JsonDocument()
            document.addRoot(convertToTransformable(null, root))
            return document
        }

        JsonNode asJacksonNode(JsonDocument document) {
            return convertToJackson(document.iterator().next())
        }


        private JsonNode convertToJackson(BaseNode baseNode) {
            if (baseNode.isArray()) {
                def result = new ArrayNode(JsonNodeFactory.instance)
                JsonArrayNode arrayNode = baseNode as JsonArrayNode
                for (BaseNode childNode : arrayNode.children) {
                    def jackson = convertToJackson(childNode)
                    for (Tuple2<String, String> pair : strategy.toJacksonInArray(childNode)) {
                        if (jackson instanceof ObjectNode) {
                            jackson.put(pair.getFirst(), pair.getSecond())
                        }
                    }
                    result.add(jackson)
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
                return NullNode.instance
            }
            if (baseNode.isObject()) {
                def result = new ObjectNode(JsonNodeFactory.instance)
                def objectNode = baseNode as JsonObjectNode

                for (BaseNode childNode : objectNode.children) {
                    result.put(strategy.toJackson(childNode), convertToJackson(childNode))
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
        private BaseNode convertToTransformable(String name, JsonNode source) {
            if (source.isObject()) {
                def result = new JsonObjectNode()
                result.identifier = strategy.toTransformable(name, source as ObjectNode)
                result.identifier.classes.add("object")

                ObjectNode object = source as ObjectNode
                Iterator<Map.Entry<String, JsonNode>> fields = object.fields
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    result.addChildNoUpdate(convertToTransformable(entry.key, entry.value))
                }
                result.updateMap()

                return result
            } else if (source.isArray()) {
                def result = new JsonArrayNode()
                result.identifier = strategy.toTransformable(name, source as ArrayNode)
                result.identifier.classes.add("array")

                ArrayNode array = source as ArrayNode
                for (JsonNode node : array) {
                    result.addChild(convertToTransformable(null, node))
                }
                return result
            } else if (source.isValueNode()) {
                def result = new JsonValueNode()
                result.identifier = strategy.toTransformable(name, source as ValueNode)
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
            return new JsonValueNode(null)
        }
    }
}
