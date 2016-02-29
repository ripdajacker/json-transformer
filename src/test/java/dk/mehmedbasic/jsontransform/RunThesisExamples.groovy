package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonType
import dk.mehmedbasic.jsonast.JsonValueNode
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import dk.mehmedbasic.jsonast.transform.MergeValueFunction
import groovy.transform.TypeChecked
import org.codehaus.jackson.map.ObjectMapper
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Examples used in my thesis.
 */
@TypeChecked
class RunThesisExamples {
    JsonDocument document

    @Before
    void prepare() {
        document = JsonDocument.parse(new FileInputStream(new File("src/main/resources/thesis-example.json")))
    }

    @Test
    void addValue() {
        println("Add value")

        document.transform("person")
                .add("age", 60d)
                .apply()
    }

    @Test
    void addObject() {
        println("Add object")

        document.transform("person")
                .add("microphone", JsonType.Object)
                .apply()

        document.transform("person microphone")
                .add("type", "cardioid")
                .apply()
    }

    @Test
    void renameChild() {
        println("Rename child")

        document.transform("person")
                .renameChild("name", "navn")
                .apply()
    }

    @Test
    void renameField() {
        println("Rename field")

        document.transform("person name")
                .renameTo("navn")
                .apply()
    }

    @Test
    void deleteField() {
        println("Delete field")
        document.transform("person")
                .deleteChild("name")
                .apply()
    }

    @Test
    void partition() {
        println("Partition object")

        document.transform("person")
                .partition([["value1", "name"], ["value2", "occupation"]])
                .apply()

        document.transform("")
                .deleteChild("person")
                .apply()
    }

    @Test
    void traversal() {
        println("Multiple transformations with hierarchy traversal")

        document.transform("person")
                .renameTo("bill")
                .apply()
                .transform("name")
                .renameTo("namen")
                .apply()
        printOut()
    }

    @Test
    void multipleAdditions() {
        println("Multiple additions")

        document.transform("person")
                .add("age", 60d)
                .add("pet", JsonType.Object)
                .apply()

                .transform("pet")
                .add("type", "dog")
                .add("name", "bingo")
                .apply()
    }


    @Test
    void verbatimAddition() {
        println("Verbatim additions")

        document.transform("person")
                .add("age", 60d)
                .addJson("pet", '{"type":"dog", "name": "bingo"}')
                .renameChild("pet", "bingo_the_dog")
                .apply()

    }


    @Test
    void move() {
        println("Move the 'name' node to a newly created array")
        document.select("person").transform().addJson("array", "[]").apply()

        document.transform("name")
                .moveTo("array")
                .apply()
    }


    @Test
    void mergeAndRename() {
        println("Merge the 'name' node with occupation, rename the node to 'name_and_occupation'")

        def mergeFunction = new MergeValueFunction() {
            @Override
            void apply(JsonValueNode source, JsonValueNode destination) {
                destination.value = "${source.stringValue()}, ${destination.stringValue()}"
            }
        }

        document
                .transform("name").merge("occupation", mergeFunction).apply()
                .select("name").parent().transform().deleteChild("name").apply()
                .transform("occupation").renameTo("name_and_occupation").apply()
    }

    @Test
    void simpleMerge() {
        println("Merge the 'name' node with occupation")

        def mergeFunction = new MergeValueFunction() {
            @Override
            void apply(JsonValueNode source, JsonValueNode destination) {
                destination.value = "${source.stringValue()}, ${destination.stringValue()}"
            }
        }

        document.transform("name").merge("occupation", mergeFunction).apply()
    }

    @Test
    void manipulateValues() {
        println("Manipulating values")

        def closure = { JsonValueNode it ->
            String newValue = it.stringValue().toLowerCase()
            it.value = newValue
        }

        document.transform("person")
                .manipulateValue("name", closure)
                .apply()
    }

    @Test
    void withClause() {
        println("Doing stuff using a 'with' clause")

        document.with {
            transform("person").add("john", "doe").apply()
            transform("name").manipulateValue {
                it.value = it.stringValue().toUpperCase()
            }.apply()
        }
    }


    @After
    void printOut() {
        def node = JacksonConverter.asJacksonNode(document)
        def string = new ObjectMapper().writer().writeValueAsString(node)
        println(string)
        println()
    }

}
