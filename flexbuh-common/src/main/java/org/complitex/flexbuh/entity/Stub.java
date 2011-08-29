package org.complitex.flexbuh.entity;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Pavel Sknar
 *         Date: 12.08.11 12:56
 */
public class Stub<T extends DomainObject> implements Serializable {

	private static final String INVALID_STUB_ID = "Invalid stub id: ";
	private static final String INVALID_STUB_TABLE = "Invalid stub table: ";

	// object id
	private Long id;
	// table name
	private String table;

	public Stub(@NotNull T o) {
		//noinspection ConstantConditions
		this(o.getId(), o.getTable());
	}

	public Stub(@NotNull Long id, @NotNull String table) {
		Validate.isTrue(id > 0, INVALID_STUB_ID + id);
		Validate.isTrue(StringUtils.isNotEmpty(table), INVALID_STUB_TABLE + table);

		this.id = id;
	}

	@NotNull
	public Long getId() {
		return id;
	}

	@NotNull
	public String getTable() {
		return table;
	}

	public static <T extends DomainObject> Stub<T> stub(@NotNull T o) {
		return new Stub<T>(o);
	}

	public boolean sameId(@NotNull T o) {
		return id.equals(o.getId());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Stub)) {
			return false;
		}

		Stub<?> stub = (Stub<?>) o;

		return id.equals(stub.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("id", id)
				.append("table", table)
				.toString();
	}

}

