package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonArrayNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonObjectNode
import groovy.transform.CompileStatic
import org.codehaus.jackson.JsonParseException
import org.codehaus.jackson.JsonParser
import org.codehaus.jackson.JsonProcessingException
import org.codehaus.jackson.JsonToken
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper

/**
 * A very close ripoff of the JsonNodeParser in Jackson
 */
@CompileStatic
class BaseNodeParser {
    ConversionNamingStrategy strategy


    BaseNodeParser(ConversionNamingStrategy strategy) {
        this.strategy = strategy
    }

    JsonDocument parse(InputStream inputStream) {
        JsonDocument document = new JsonDocument()

        def mapper = new ObjectMapper()
        def parser = mapper.jsonFactory.createJsonParser(inputStream)

        initForReading(parser)

        document.roots.add(deserializeAny(parser))

        parser.clearCurrentToken()
        return document
    }

    private
    static JsonToken initForReading(JsonParser jp) throws IOException, JsonParseException, JsonMappingException {
        /* First: must point to a token; if not pointing to one, advance.
         * This occurs before first read from JsonParser, as well as
         * after clearing of current token.
         */
        JsonToken t = jp.getCurrentToken();
        if (t == null) {
            // and then we must get something...
            t = jp.nextToken();
            if (t == null) {
                /* [JACKSON-99] Should throw EOFException, closed thing
                 *   semantically
                 */
                throw new EOFException("No content to map to Object due to end of input");
            }
        }
        return t;
    }

    private final JsonObjectNode deserializeObject(JsonParser parser) throws IOException, JsonProcessingException {
        JsonObjectNode node = JsonDocument.createObjectNode();
        JsonToken token = parser.getCurrentToken();
        if (token == JsonToken.START_OBJECT) {
            token = parser.nextToken();
        }
        for (; token == JsonToken.FIELD_NAME; token = parser.nextToken()) {
            String fieldName = parser.getCurrentName();
            parser.nextToken();
            BaseNode value = deserializeAny(parser);
            value.identifier = strategy.toTransformableName(fieldName, node)
            node.addChild(value);
        }
        return node;
    }

    private final JsonArrayNode deserializeArray(JsonParser parser)
            throws IOException, JsonProcessingException {
        JsonArrayNode node = JsonDocument.createArrayNode()
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            node.addChild(deserializeAny(parser));
        }
        return node;
    }


    private final BaseNode deserializeAny(JsonParser parser)
            throws IOException, JsonProcessingException {
        switch (parser.getCurrentToken()) {
            case JsonToken.START_OBJECT:
            case JsonToken.FIELD_NAME:
                return deserializeObject(parser);

            case JsonToken.START_ARRAY:
                return deserializeArray(parser);

            case JsonToken.VALUE_STRING:
                return JsonDocument.createTextNode(parser.getText())
            case JsonToken.VALUE_NUMBER_INT:

                def node = JsonDocument.createValueNode()
                JsonParser.NumberType nt = parser.getNumberType();
                if (nt == JsonParser.NumberType.INT) {
                    node.setValue(parser.getIntValue() as int)
                } else {
                    node.setValue(parser.getLongValue() as long)
                }
                return node

            case JsonToken.VALUE_NUMBER_FLOAT:
                return JsonDocument.createNumberNode(parser.getDoubleValue());
            case JsonToken.VALUE_TRUE:
                return JsonDocument.createBooleanNode(true);

            case JsonToken.VALUE_FALSE:
                return JsonDocument.createBooleanNode(false);

            case JsonToken.VALUE_NULL:
                return JsonDocument.createTextNode(null);

        // These states can not be mapped; input stream is
        // off by an event or two

            case JsonToken.END_OBJECT:
            case JsonToken.END_ARRAY:
            default:
                throw new RuntimeException("Something went wrong - not valid JSON");
        }
    }

}
