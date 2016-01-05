package dk.mehmedbasic.jsonast

import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import org.codehaus.jackson.map.ObjectMapper

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

}
