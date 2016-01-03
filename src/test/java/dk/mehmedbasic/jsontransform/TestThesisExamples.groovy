package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonType
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import groovy.transform.TypeChecked
import org.codehaus.jackson.map.ObjectMapper
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Examples used in my thesis.
 */
@TypeChecked
class TestThesisExamples {
    JsonDocument document

    @Before
    void prepare() {
        def mapper = new ObjectMapper()
        def tree = mapper.readTree(new FileInputStream(new File("src/main/resources/thesis-example.json")))
        document = JacksonConverter.asTransformable(tree)
    }

    @Test
    void addValue() {
        document.transform("person")
                .add("age", 60d)
                .apply()
    }

    @Test
    void addObject() {
        document.transform("person")
                .add("microphone", JsonType.Object)
                .apply()

        document.transform("person microphone")
                .add("type", "cardioid")
                .apply()
    }

    @After
    void printOut() {
        def node = JacksonConverter.asJacksonNode(document)
        def string = new ObjectMapper().writer().writeValueAsString(node)
        println(string)
    }

}
