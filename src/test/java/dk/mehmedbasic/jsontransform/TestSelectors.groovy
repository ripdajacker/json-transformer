package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonObjectNode
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests selectors
 */
class TestSelectors {
    JsonDocument document

    @Before
    void prepare() {
        def mapper = new ObjectMapper()
        def tree = mapper.readTree(new FileInputStream(new File("src/main/resources/move-rename.json")))
        document = JacksonConverter.convert(tree)
    }

    @Test
    void selectByName() {
        def names = document.select("name")
        Assert.assertEquals("Names should have three elements", 3, names.length)
        Assert.assertEquals("First element should be value 'Jon Snow'", "Jon Snow", names.roots[0].stringValue())
        Assert.assertEquals("Second element should be value 'Aemon Targaryen'", "Aemon Targaryen", names.roots[1].stringValue())
        Assert.assertEquals("Second element should be value 'Plague-infested Rat'", "Plague-infested Rat", names.roots[2].stringValue())
    }

    @Test
    void selectWithPrefix() {
        Assert.assertEquals("Jon should have zero elements", 0, document.select("residents[name^=Jon]").length)

        def prefixed = document.select("residents .object[name^=Plague]")
        Assert.assertEquals("Should have one element", 1, prefixed.length)

        def rat = prefixed.selectSingle(null).get()
        Assert.assertTrue("Selected should be instance of JsonObjectNode", rat instanceof JsonObjectNode)
        Assert.assertEquals("Selected should have name= 'Plague-infested Rat'", 'Plague-infested Rat', rat.get("name").stringValue())
    }
}
