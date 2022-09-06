package dk.mehmedbasic.jsontransform;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonNodes;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests selectors
 */
public class TestSelectors {

  private JsonDocument document;

  @Before
  public void prepare() throws IOException {
    document = JsonDocument.parse(
        new FileInputStream("src/main/resources/move-rename.json"));
  }

  @Test
  public void selectByName() {
    JsonNodes names = document.select("name");
    JsonAssert.assertRootCount(names, 3);

    List<BaseNode> roots = new ArrayList<>(names.getRoots());
    JsonAssert.assertStringValue(roots.get(0), "Jon Snow");
    JsonAssert.assertStringValue(roots.get(1), "Aemon Targaryen");
    JsonAssert.assertStringValue(roots.get(2), "Ratty McRatson");
  }

  @Test
  public void selectWithPrefix() {
    JsonNodes prefixed = document.select("residents .object[name^=Ratty]");
    Assert.assertEquals("Should have one element", 1, prefixed.getRootCount());

    BaseNode rat = prefixed.selectSingle(null).orElseThrow();
    JsonAssert.assertStringValue(rat.get("name"), "Ratty McRatson");
  }
}
