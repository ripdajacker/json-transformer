package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonObjectNode
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
        document = JsonDocument.parse(new FileInputStream(new File("src/main/resources/move-rename.json")))
    }

    @Test
    void selectByName() {
        def names = document.select("name")
        Assert.assertEquals("Names should have three elements", 3, names.length)
        Assert.assertEquals("First element should be value 'Jon Snow'", "Jon Snow", names.roots[0].stringValue())
        Assert.assertEquals("Second element should be value 'Aemon Targaryen'", "Aemon Targaryen", names.roots[1].stringValue())
        Assert.assertEquals("Second element should be value 'Ratty McRatson'", "Ratty McRatson", names.roots[2].stringValue())
    }

    @Test
    void selectWithPrefix() {
        def prefixed = document.select("residents .object[name^=Ratty]")
        Assert.assertEquals("Should have one element", 1, prefixed.length)

        def rat = prefixed.selectSingle(null).get()
        Assert.assertTrue("Selected should be instance of JsonObjectNode", rat instanceof JsonObjectNode)
        Assert.assertEquals("Selected should have name= 'Ratty McRatson'", 'Ratty McRatson', rat.get("name").stringValue())
    }
}
