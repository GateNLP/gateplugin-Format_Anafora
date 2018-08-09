package gate.creole.anafora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import gate.creole.anafora.AnaforaSchema.Entity;

public class AnaforaSchemaTest {

  @Test
  public void testSchemaLoading() throws Exception {
    AnaforaSchema schema = new AnaforaSchema(
        this.getClass().getResource("/schemas/demomedicalschema.xml"));

    assertEquals("Unexpected number of entity types", 4,
        schema.getEntityTypes().size());

    Entity entity = schema.getEntity("Disease/Disorder");

    assertNotNull("Unable to find expected entity type", entity);

    assertEquals("Unexpected number of properties", 4,
        entity.getProperties().size());
  }
}
