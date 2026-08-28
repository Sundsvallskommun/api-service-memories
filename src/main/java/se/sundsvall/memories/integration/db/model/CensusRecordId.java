package se.sundsvall.memories.integration.db.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite id for {@link CensusRecordEntity}: the census volume ({@code KALLA}, e.g. {@code 1845}) and the row number
 * within it. Each volume numbers its rows from 1, so neither part is unique on its own.
 */
public class CensusRecordId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String source;
	private Integer id;

	public CensusRecordId() {}

	public CensusRecordId(final String source, final Integer id) {
		this.source = source;
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final CensusRecordId that = (CensusRecordId) o;
		return Objects.equals(source, that.source) && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, id);
	}

	@Override
	public String toString() {
		return "CensusRecordId{" +
			"source='" + source + '\'' +
			", id=" + id +
			'}';
	}
}
