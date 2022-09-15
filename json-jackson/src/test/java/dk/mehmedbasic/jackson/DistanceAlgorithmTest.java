package dk.mehmedbasic.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 * Distance tests
 */
public class DistanceAlgorithmTest {

  private JsonDocument document;

  @Before
  public void prepare() throws IOException {
    document = JsonDocument.parse(
        new FileInputStream("src/main/resources/distance-test.json"));
  }

  @Test
  public void precalculatedDistances() {
    BaseNode eNode = document.selectSingle("E").orElseThrow();

    assertThat(distanceBetween(eNode, "Y")).isEqualTo(5);
    assertThat(distanceBetween(eNode, "Z")).isEqualTo(4);
    assertThat(distanceBetween(eNode, "X")).isEqualTo(3);
    assertThat(distanceBetween(eNode, "H")).isEqualTo(2);
  }

  private int distanceBetween(BaseNode eNode, String Y) {
    return eNode.distanceTo(document.selectSingle(Y).orElseThrow());
  }
}
