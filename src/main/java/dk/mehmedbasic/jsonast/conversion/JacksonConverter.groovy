package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.*
import groovy.transform.CompileStatic
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.node.*

/**
 * Converts jackson nodes to dk.mehmedbasic.jsonast nodes.
 */
@CompileStatic
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
            if (baseNode.array) {
                def result = new ArrayNode(JsonNodeFactory.instance)
                JsonArrayNode arrayNode = baseNode as JsonArrayNode
                for (BaseNode childNode : arrayNode.children) {
                    def jackson = convertToJackson(childNode)
                    if (jackson instanceof ObjectNode) {
                        for (Tuple2<String, String> pair : strategy.toJacksonInArray(childNode)) {
                            jackson.put(pair.first, pair.second)
                        }
                    }
                    result.add(jackson)
                }
                return result
            }
            if (baseNode.valueNode) {
                JsonValueNode valueNode = baseNode as JsonValueNode
                if (baseNode.int) {
                    return new IntNode(valueNode.intValue())
                } else if (baseNode.double) {
                    return new DoubleNode(valueNode.doubleValue())
                } else if (baseNode.boolean) {
                    if (valueNode.booleanValue()) {
                        return BooleanNode.TRUE
                    } else {
                        return BooleanNode.FALSE
                    }
                } else if (baseNode.string) {
                    return new TextNode(valueNode.stringValue())
                }
                return NullNode.instance
            }
            if (baseNode.object) {
                def result = new ObjectNode(JsonNodeFactory.instance)
                def objectNode = baseNode as JsonObjectNode

                for (BaseNode childNode : objectNode.children) {
                    result.put(strategy.toJacksonName(childNode), convertToJackson(childNode))
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
            if (source.object) {
                def result = new JsonObjectNode()
                def objectNode = source as ObjectNode

                result.identifier = strategy.toTransformableName(name, result)
                result.identifier.classes.add("object")

                Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    result.addChild(convertToTransformable(entry.key, entry.value))
                }
                return result
            } else if (source.array) {
                def result = new JsonArrayNode()
                def arrayNode = source as ArrayNode

                result.identifier = strategy.toTransformableName(name, result)
                result.identifier.classes.add("array")

                ArrayNode array = arrayNode
                for (JsonNode node : array) {
                    result.addChild(convertToTransformable(null, node))
                }
                return result
            } else if (source.valueNode) {
                def result = new JsonValueNode()
                result.identifier = strategy.toTransformableName(name, result)
                if (name == "@version") {
                    result.identifier.classes << "sysclass_version"
                }

                result.identifier.classes.add("value")

                if (source.boolean) {
                    result.identifier.classes.add("boolean")
                    result.setValue(source.booleanValue)
                } else if (source.int) {
                    result.identifier.classes.add("int")
                    result.setValue(source.intValue)
                } else if (source.textual) {
                    result.identifier.classes.add("string")
                    result.value = source.textValue
                } else if (source.double) {
                    result.identifier.classes.add("double")
                    result.setValue(source.doubleValue)
                }
                return result
            }
            return new JsonValueNode(null)
        }
    }
}
