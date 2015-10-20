package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonValueNode
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import dk.mehmedbasic.jsonast.transform.ManipulateValueFunction
import dk.mehmedbasic.jsonast.transform.MergeValueFunction
import dk.mehmedbasic.jsonast.transform.Transformer
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ObjectNode
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Test transforms
 */
class TestTransform {
    JsonDocument document

    @Before
    void setup() {
        def mapper = new ObjectMapper()
        def tree = mapper.readTree(new FileInputStream(new File("src/main/resources/move-rename.json")))
        document = JacksonConverter.convert(tree as ObjectNode)
    }

    @Test
    void rename() {

        new Transformer("residents").renameTo("ned").apply(document)

        def nodes = document.select("ned")

        Assert.assertEquals("There should be two 'ned' nodes", 2, nodes.length)
    }

    @Test
    void moveUpward() {
        new Transformer("residents").moveTo("ned").apply(document)

        def residents = document.selectSingle("residents").get()
        Assert.assertEquals("Name of residents parent should be 'ned'", "ned", residents.parent.identifier.name)
        Assert.assertEquals("'ned' should have two children", 2, residents.parent.length)

        def castle = document.selectSingle("castle-black").get()
        Assert.assertEquals("Element castle-black parent should have 0 children", 0, castle.length)
    }

    @Test
    void moveToSibling() {
        def names = document.select("name")

        Assert.assertEquals("Names should have three elements", 3, names.length)
        Assert.assertEquals("First element should be value 'Jon Snow'", "Jon Snow", names.roots[0].stringValue())
        Assert.assertEquals("Second element should be value 'Aemon Targaryen'", "Aemon Targaryen", names.roots[1].stringValue())
        Assert.assertEquals("Second element should be value 'Ratty McRatson'", "Ratty McRatson", names.roots[2].stringValue())
    }

    @Test
    void manipulateString() {
        def before = document.selectSingle("status").get()
        Assert.assertEquals("Status should be 'alive'", "alive", before.value)

        def function = new ManipulateValueFunction() {
            @Override
            void apply(JsonValueNode node) {
                node.value = node.value + " until end of Season 5"
            }
        }

        new Transformer("status").manipulateValue(function).apply(document)

        def after = document.selectSingle("status").get()
        Assert.assertEquals("Status should be 'alive until end of Season 5'", "alive until end of Season 5", after.value)
    }

    @Test
    void manipulateInt() {
        def selector = "age"
        def before = document.selectSingle(selector).get()
        Assert.assertEquals("Age should be '16'", 16, before.value)

        def ageOneYear = new ManipulateValueFunction() {
            @Override
            void apply(JsonValueNode node) {
                node.value = node.value + 1
            }
        }

        new Transformer(selector).manipulateValue(ageOneYear).apply(document)

        def ages = document.select(selector)
        Assert.assertEquals("Age should be '17'", 17, ages.roots[0].value)
        Assert.assertEquals("Age should be '105'", 105, ages.roots[1].value)
    }


    @Test
    void mergeStrings() {
        def selector = "title"
        def before = document.selectSingle(selector).get()
        Assert.assertEquals("$selector should be 'Maester'", "Maester", before.value)

        def function = new MergeValueFunction() {
            @Override
            void apply(JsonValueNode source, JsonValueNode destination) {
                destination.value = destination.value + ", " + source.value
            }
        }
        new Transformer(selector)
                .merge("castle-black name", function)
                .apply(document)

        def ages = document.select("name")
        Assert.assertEquals("name should be 'Jon Snow'", "Jon Snow", ages.roots[0].value)
        Assert.assertEquals("name should be 'Aemon Targaryen, Maester'", "Aemon Targaryen, Maester", ages.roots[1].value)
    }

    @Test
    void deleteChild() {
        def selector = "title"
        def before = document.selectSingle(selector).get()
        Assert.assertEquals("$selector should be 'Maester'", "Maester", before.value)

        new Transformer("castle-black .object")
                .deleteChild("title")
                .apply(document)


        def ages = document.select(selector)
        Assert.assertEquals("Selection should have zero children", 0, ages.length)
        Assert.assertEquals("Before node should have null parent", null, before.parent)
    }

}
