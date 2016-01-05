package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.*
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import dk.mehmedbasic.jsonast.transform.ManipulateValueFunction
import dk.mehmedbasic.jsonast.transform.MergeValueFunction
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
        document = JacksonConverter.asTransformable(tree as ObjectNode)
    }

    @Test
    void rename() {
        document.transform("residents").renameTo("ned").apply()

        def nodes = document.select("ned")

        Assert.assertEquals("There should be two 'ned' nodes", 2, nodes.length)
    }

    @Test
    void moveUpward() {
        document.transform("residents").moveTo("ned").apply()

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

        document.transform("status").manipulateValue(function).apply()

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

        document.transform(selector).manipulateValue(ageOneYear).apply()

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

        document.transform(selector)
                .merge("castle-black name", function)
                .apply()

        def ages = document.select("name")
        Assert.assertEquals("name should be 'Jon Snow'", "Jon Snow", ages.roots[0].value)
        Assert.assertEquals("name should be 'Aemon Targaryen, Maester'", "Aemon Targaryen, Maester", ages.roots[1].value)
    }

    @Test
    void deleteChildByName() {
        def selector = "title"
        def before = document.selectSingle(selector).get()
        Assert.assertEquals("$selector should be 'Maester'", "Maester", before.value)

        document.transform("castle-black .object")
                .deleteChild("title")
                .apply()


        def titles = document.select(selector)
        Assert.assertEquals("Selection should have zero children", 0, titles.length)
        Assert.assertEquals("Before node should have null parent", null, before.parent)
    }

    @Test
    void deleteChildByIndex() {
        def selector = "residents"
        def before = document.selectSingle(selector).get()
        Assert.assertEquals("$selector should have two children", 2, before.length)

        document.transform("castle-black residents")
                .deleteChild(0)
                .apply()


        def residents = document.select(selector)
        Assert.assertEquals("Selection should have 1 child", 1, residents.length)

        def residentsArray = residents.roots.get(0)
        def child = residentsArray.get(0)
        Assert.assertEquals("The child should be Ratty McRatson", "Ratty McRatson", child.get("name").value)
    }

    @Test
    void addSimpleValue() {
        def jonSnow = document.selectSingle("son").get()

        def addedSelector = "addedValue"
        Assert.assertNull("The object should not have a value named 'addedValue'", jonSnow.get(addedSelector))

        document.transform("son").add(addedSelector, 42d).apply()

        Assert.assertNotNull("The object should now have a value named 'addedValue'", jonSnow.get(addedSelector))
        def value = jonSnow.get(addedSelector).value as double

        Assert.assertEquals("The value of the added node should be 42", 42d, value, 0.0001d)
    }

    @Test
    void addArray() {
        def jonSnow = document.selectSingle("son").get()

        def addedSelector = "addedValue"
        Assert.assertNull("The object should not have a value named 'addedValue'", jonSnow.get(addedSelector))

        document.transform("son")
                .add(addedSelector, JsonType.Array)
                .apply()

        document.transform(addedSelector)
                .addValue(42d)
                .apply()

        Assert.assertNotNull("The object should now have a value named 'addedValue'", jonSnow.get(addedSelector))
        Assert.assertTrue("The new node should be an array", jonSnow.get(addedSelector).array)

        def arrayNode = jonSnow.get(addedSelector) as JsonArrayNode
        Assert.assertEquals("The array should have one node", 1, arrayNode.length)

        def value = arrayNode.get(0).value as double
        Assert.assertEquals("The value of the added node should be 42", 42d, value, 0.0001d)
    }

    @Test
    void addObject() {
        def jonSnow = document.selectSingle("son").get()

        def addedSelector = "addedValue"
        Assert.assertNull("The object should not have a value named 'addedValue'", jonSnow.get(addedSelector))

        document.transform("son")
                .add(addedSelector, JsonType.Object)
                .apply()

        document.transform(addedSelector)
                .add("fortyTwo", 42d)
                .apply()

        def newObject = jonSnow.get(addedSelector)
        Assert.assertNotNull("The object should now have a value named 'addedValue'", newObject)
        Assert.assertTrue("The new node should be an array", newObject.object)

        def objectNode = jonSnow.get(addedSelector) as JsonObjectNode
        Assert.assertEquals("The array should have one node", 1, objectNode.length)

        def value = objectNode.get("fortyTwo").value as double
        Assert.assertEquals("The value of the added node should be 42", 42d, value, 0.0001d)
    }

    @Test
    void partitionNode() {
        def ned = document.selectSingle("ned").get()

        Assert.assertEquals("Ned should have 1 child", 1, ned.length)

        document.transform("son")
                .partition([["name_and_status", "name", "status"], ["age2", "age"]])
                .apply()

        ned = document.selectSingle("ned").get()
        Assert.assertEquals("Ned should now have 3 children", 3, ned.length)


        def nameAndStatus = ned.get("name_and_status")
        Assert.assertNotNull(nameAndStatus)
        Assert.assertNotNull(ned.get("age2"))
        Assert.assertNotNull(ned.get("son"))


        Assert.assertNotNull(nameAndStatus.get("name"))
        Assert.assertNotNull(nameAndStatus.get("status"))


        def nameNode = nameAndStatus.get("name")
        Assert.assertTrue("Node should be a JsonValueNode", nameNode instanceof JsonValueNode)
        Assert.assertEquals("Name should be Jon", "Jon Snow", nameNode.stringValue())
        Assert.assertEquals("Status should be alive", "alive", nameAndStatus.get("status").stringValue())

        def node = JacksonConverter.asJacksonNode(document)
        def string = new ObjectMapper().writer().writeValueAsString(node)
        println(string)
    }

}
