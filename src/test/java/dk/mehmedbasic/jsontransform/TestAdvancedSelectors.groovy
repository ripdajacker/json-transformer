package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import groovy.transform.TypeChecked
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests some more selectors
 */
@TypeChecked
class TestAdvancedSelectors {
    JsonDocument document

    @Before
    void prepare() {
        document = JsonDocument.parse(new FileInputStream(new File("src/main/resources/move-rename.json")))
    }

    @Test
    void selectWithAttribute() {
        def selection = document.select("[name]")
        Assert.assertEquals("Selection should have three results", 3, selection.size())
    }

    @Test
    void selectWithAttributeEquals() {
        def selection = document.select("[name='Ratty McRatson']")
        Assert.assertEquals("Selection should have one result", 1, selection.size())
    }

    @Test
    void selectWithAttributeSubstring() {
        def selection = document.select("[name*='Ratty']")
        Assert.assertEquals("Selection should have one result", 1, selection.size())
    }

    @Test
    void testfdsafdsa() {
        def selection = document.select("name.foo")
        Assert.assertEquals("Selection should have one result", 1, selection.size())
    }
}
