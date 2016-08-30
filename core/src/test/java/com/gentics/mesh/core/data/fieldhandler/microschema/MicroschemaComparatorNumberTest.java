package com.gentics.mesh.core.data.fieldhandler.microschema;

import com.gentics.mesh.FieldUtil;
import com.gentics.mesh.core.data.fieldhandler.AbstractComparatorNumberTest;
import com.gentics.mesh.core.data.schema.handler.AbstractFieldSchemaContainerComparator;
import com.gentics.mesh.core.data.schema.handler.MicroschemaComparator;
import com.gentics.mesh.core.rest.schema.Microschema;

public class MicroschemaComparatorNumberTest extends AbstractComparatorNumberTest<Microschema> {

	@Override
	public AbstractFieldSchemaContainerComparator<Microschema> getComparator() {
		return new MicroschemaComparator();
	}

	@Override
	public Microschema createContainer() {
		return FieldUtil.createMinimalValidMicroschema();
	}

}