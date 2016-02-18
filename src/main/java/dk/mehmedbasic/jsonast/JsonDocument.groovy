package dk.mehmedbasic.jsonast

import com.google.common.base.Charsets
import dk.mehmedbasic.jsonast.conversion.BaseNodeParser
import dk.mehmedbasic.jsonast.conversion.InlineIdsNamingStrategy
import groovy.transform.CompileStatic

/**
 * A json document.
 *
 * This is used as the base class for selection and transformation.
 */
@CompileStatic
public class JsonDocument extends JsonNodes {

    JsonDocument() {
        document = this
    }

    /**
     * Creates a value node.
     *
     * @return the node.
     */
    static JsonValueNode createValueNode() {
        def node = new JsonValueNode()
        return node
    }

    /**
     * Creates a text node.
     *
     * @return the node.
     */
    static JsonValueNode createTextNode(String value) {
        def node = new JsonValueNode()
        node.value = value
        return node
    }

    /**
     * Creates a number value node.
     *
     * @return the node.
     */
    static JsonValueNode createNumberNode(double value) {
        def node = createValueNode()
        node.setValue(value)
        return node
    }

    /**
     * Creates a boolean value node.
     *
     * @return the node.
     */
    static JsonValueNode createBooleanNode(boolean value) {
        def node = createValueNode()
        node.setValue(value)
        return node
    }

    /**
     * Creates an array node.
     *
     * @return the node.
     */
    static JsonArrayNode createArrayNode() {
        return new JsonArrayNode()
    }

    /**
     * Creates an object node.
     *
     * @return the node.
     */
    static JsonObjectNode createObjectNode() {
        return new JsonObjectNode()
    }

    /**
     * Parses a JsonDocument from the given input.
     *
     * @param inputStream the stream to parse.
     *
     * @return the resulting JsonDocument.
     */
    static JsonDocument parse(InputStream inputStream) {
        def parser = new BaseNodeParser(new InlineIdsNamingStrategy())
        return parser.parse(inputStream)
    }

    /**
     * Parses a JsonDocument from the given input.
     *
     * @param content the string to parse.
     *
     * @return the resulting JsonDocument.
     */
    static JsonDocument parse(String content) {
        def parser = new BaseNodeParser(new InlineIdsNamingStrategy())
        return parser.parse(new ByteArrayInputStream(content.getBytes(Charsets.UTF_8)))
    }
}
