package dk.mehmedbasic.jsontransform;

import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonNodes;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests some more selectors
 */
public class TestAdvancedSelectors {

  private JsonDocument document;

  @Before
  public void prepare() throws IOException {
    document = JsonDocument.parse(
        new FileInputStream("src/main/resources/move-rename.json"));
  }

  @Test
  public void selectWithAttribute() {
    JsonNodes selection = document.select("[name]");
    JsonAssert.assertRootCount(selection, 3);
  }

  @Test
  public void selectWithAttributeEquals() {
    JsonNodes selection = document.select("[name='Ratty McRatson']");
    JsonAssert.assertRootCount(selection, 1);
  }

  @Test
  public void selectWithAttributeSubstring() {
    JsonNodes selection = document.select("[name*='Ratty']");
    JsonAssert.assertRootCount(selection, 1);
  }
}
