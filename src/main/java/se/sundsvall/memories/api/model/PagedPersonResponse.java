package se.sundsvall.memories.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "Paged person response")
public class PagedPersonResponse {

	@ArraySchema(schema = @Schema(implementation = Person.class, accessMode = READ_ONLY))
	private List<Person> persons;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = READ_ONLY)
	private PagingAndSortingMetaData metaData;

	public static PagedPersonResponse create() {
		return new PagedPersonResponse();
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(final List<Person> persons) {
		this.persons = persons;
	}

	public PagedPersonResponse withPersons(final List<Person> persons) {
		this.persons = persons;
		return this;
	}

	public PagingAndSortingMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
	}

	public PagedPersonResponse withMetaData(final PagingAndSortingMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final PagedPersonResponse that = (PagedPersonResponse) o;
		return Objects.equals(persons, that.persons) && Objects.equals(metaData, that.metaData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(persons, metaData);
	}

	@Override
	public String toString() {
		return "PagedPersonResponse{" +
			"persons=" + persons +
			", metaData=" + metaData +
			'}';
	}
}
