package com.gentics.mesh.core.data.fieldhandler.schema;

import static com.gentics.mesh.assertj.MeshAssertions.assertThat;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.CONTAINER_FLAG_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.DESCRIPTION_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.DISPLAY_FIELD_NAME_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.FIELD_ORDER_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.NAME_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.SEGMENT_FIELD_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel.TYPE_KEY;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeOperation.CHANGEFIELDTYPE;
import static com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeOperation.UPDATESCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.gentics.mesh.FieldUtil;
import com.gentics.mesh.core.data.schema.handler.SchemaComparator;
import com.gentics.mesh.core.rest.schema.Schema;
import com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeModel;
import com.gentics.mesh.core.rest.schema.change.impl.SchemaChangeOperation;
import com.gentics.mesh.test.AbstractDBTest;

public class SchemaComparatorSchemaTest extends AbstractDBTest {

	private SchemaComparator comparator = new SchemaComparator();

	@Test
	public void testEmptySchema() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testChangeFieldType() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		schemaA.addField(FieldUtil.createStringFieldSchema("content"));
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.addField(FieldUtil.createNumberFieldSchema("content"));

		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(CHANGEFIELDTYPE).hasProperty("field", "content").hasProperty(TYPE_KEY, "number");
	}

	@Test
	public void testSchemaFieldReorder() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		schemaA.addField(FieldUtil.createHtmlFieldSchema("first"));
		schemaA.addField(FieldUtil.createHtmlFieldSchema("second"));

		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.addField(FieldUtil.createHtmlFieldSchema("second"));
		schemaB.addField(FieldUtil.createHtmlFieldSchema("first"));
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(FIELD_ORDER_KEY, new String[] { "displayFieldName", "second", "first" });
	}

	@Test
	public void testSegmentFieldAdded() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.setSegmentField(schemaB.getFields().get(0).getName());
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertEquals(SchemaChangeOperation.UPDATESCHEMA, changes.get(0).getOperation());
	}

	@Test
	public void testSegmentFieldRemoved() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setSegmentField(schemaA.getFields().get(0).getName());
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertEquals(SchemaChangeOperation.UPDATESCHEMA, changes.get(0).getOperation());
	}

	@Test
	public void testSchemaFieldNoReorder() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		schemaA.addField(FieldUtil.createHtmlFieldSchema("first"));
		schemaA.addField(FieldUtil.createHtmlFieldSchema("second"));

		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.addField(FieldUtil.createHtmlFieldSchema("first"));
		schemaB.addField(FieldUtil.createHtmlFieldSchema("second"));
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testSegmentFieldSame() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		schemaA.setSegmentField(schemaA.getFields().get(0).getName());
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.setSegmentField(schemaA.getSegmentField());
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testSegmentFieldUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		schemaA.addField(FieldUtil.createStringFieldSchema("someExtraField"));
		schemaA.setSegmentField("someExtraField");

		// Change segment field from the extrafield to the displayfield
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.addField(FieldUtil.createStringFieldSchema("someExtraField"));
		schemaB.setSegmentField("displayFieldName");

		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(SEGMENT_FIELD_KEY, "displayFieldName");
	}

	@Test
	public void testDisplayFieldUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		schemaA.addField(FieldUtil.createStringFieldSchema("someExtraField"));
		schemaA.setDisplayField("displayFieldName");

		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaB.addField(FieldUtil.createStringFieldSchema("someExtraField"));
		schemaB.setDisplayField("someExtraField");

		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(DISPLAY_FIELD_NAME_KEY, "someExtraField");
	}

	@Test
	public void testDisplayFieldSame() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testContainerFlagUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setContainer(true);
		schemaB.setContainer(false);
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(CONTAINER_FLAG_KEY, false);
	}

	@Test
	public void testContainerFlagSame() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setContainer(true);
		schemaB.setContainer(true);
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();

		schemaA.setContainer(false);
		schemaB.setContainer(false);
		changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testSameDescription() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setDescription("test123");
		schemaB.setDescription("test123");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testDescriptionUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setDescription("test123");
		schemaB.setDescription("test123-changed");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(DESCRIPTION_KEY, "test123-changed");
	}

	@Test
	public void testDescriptionUpdatedToNull() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setDescription("test123");
		schemaB.setDescription(null);
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(DESCRIPTION_KEY, null);
	}

	@Test
	public void testSameName() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setName("test123");
		schemaB.setName("test123");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).isEmpty();
	}

	@Test
	public void testNameUpdated() throws IOException {
		Schema schemaA = FieldUtil.createMinimalValidSchema();
		Schema schemaB = FieldUtil.createMinimalValidSchema();
		schemaA.setName("test123");
		schemaB.setName("test123-changed");
		List<SchemaChangeModel> changes = comparator.diff(schemaA, schemaB);
		assertThat(changes).hasSize(1);
		assertThat(changes.get(0)).is(UPDATESCHEMA).hasProperty(NAME_KEY, "test123-changed");
	}

}