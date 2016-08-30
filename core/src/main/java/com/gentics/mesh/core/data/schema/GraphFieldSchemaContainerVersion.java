package com.gentics.mesh.core.data.schema;

import com.gentics.mesh.context.InternalActionContext;
import com.gentics.mesh.core.data.MeshCoreVertex;
import com.gentics.mesh.core.data.ReferenceableElement;
import com.gentics.mesh.core.data.schema.handler.AbstractFieldSchemaContainerComparator;
import com.gentics.mesh.core.rest.common.GenericMessageResponse;
import com.gentics.mesh.core.rest.common.NameUuidReference;
import com.gentics.mesh.core.rest.schema.FieldSchemaContainer;
import com.gentics.mesh.core.rest.schema.change.impl.SchemaChangesListModel;

import rx.Single;

/**
 * A {@link GraphFieldSchemaContainerVersion} stores the versioned data for a {@link GraphFieldSchemaContainer} element.
 * 
 * @param <R>
 *            Rest model type
 * @param <RE>
 *            Reference model type
 * @param <SCV>
 *            Schema container version type
 * @param <SC>
 *            Schema container type
 */
public interface GraphFieldSchemaContainerVersion<R extends FieldSchemaContainer, RE extends NameUuidReference<RE>, SCV extends GraphFieldSchemaContainerVersion<R, RE, SCV, SC>, SC extends GraphFieldSchemaContainer<R, RE, SC, SCV>>
		extends MeshCoreVertex<R, SCV>, ReferenceableElement<RE> {

	/**
	 * Return the schema version.
	 * 
	 * @return
	 */
	int getVersion();

	/**
	 * Return the schema that is stored within the container.
	 * 
	 * @return
	 */
	R getSchema();

	/**
	 * Set the schema for the container.
	 * 
	 * @param schema
	 */
	void setSchema(R schema);

	/**
	 * Return the change for the previous version of the schema. Normally the previous change was used to build the schema.
	 * 
	 * @return
	 */
	SchemaChange<?> getPreviousChange();

	/**
	 * Return the change for the next version.
	 * 
	 * @return Can be null if no further changes exist
	 */
	SchemaChange<?> getNextChange();

	/**
	 * Set the next change for the schema. The next change is the first change in the chain of changes that lead to the new schema version.
	 * 
	 * @param change
	 */
	void setNextChange(SchemaChange<?> change);

	/**
	 * Set the previous change for the schema. The previous change is the last change in the chain of changes that was used to create the schema container.
	 * 
	 * @param change
	 */
	void setPreviousChange(SchemaChange<?> change);

	/**
	 * Return the next version of this schema.
	 * 
	 * @return
	 */
	SCV getNextVersion();

	/**
	 * Set the next version of the schema container.
	 * 
	 * @param container
	 */
	void setNextVersion(SCV container);

	/**
	 * Return the previous version of this schema.
	 * 
	 * @return
	 */
	SCV getPreviousVersion();

	/**
	 * Set the previous version of the container.
	 * 
	 * @param container
	 */
	void setPreviousVersion(SCV container);

	/**
	 * Generate a schema change list by comparing the schema with the specified schema update model which is extracted from the action context.
	 * 
	 * @param ac
	 *            Action context that provides the schema update request
	 * @param comparator
	 *            Comparator to be used to compare the schemas
	 * @param restModel
	 *            Rest model of the container that should be compared
	 * @return
	 */
	Single<SchemaChangesListModel> diff(InternalActionContext ac, AbstractFieldSchemaContainerComparator<?> comparator,
			FieldSchemaContainer restModel);

	/**
	 * Apply changes which will be extracted from the action context.
	 * 
	 * @param ac
	 *            Action context that provides the migration request data
	 * @return
	 */
	Single<GenericMessageResponse> applyChanges(InternalActionContext ac);

	/**
	 * Apply the given list of changes to the schema container. This method will invoke the schema migration process.
	 * 
	 * @param ac
	 * @param listOfChanges
	 */
	Single<GenericMessageResponse> applyChanges(InternalActionContext ac, SchemaChangesListModel listOfChanges);

	/**
	 * Return the parent schema container of the version.
	 * 
	 * @return
	 */
	SC getSchemaContainer();

	/**
	 * Set the parent schema container of this version.
	 */
	void setSchemaContainer(SC container);
}