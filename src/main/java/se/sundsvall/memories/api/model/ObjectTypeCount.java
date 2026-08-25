package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/** One chip counter. The type is a value rather than a JSON key, which keeps the archive's spelling out of the keys. */
@Schema(description = "How many objects of one type the search matches")
public class ObjectTypeCount {

	@Schema(description = "Object type (Foto, Föremål, Film, Ljud, Text, Publikation, Person, Juridisk person or Sjöman) — the same value each object reports and the objectType filter accepts",
		examples = "Foto",
		accessMode = READ_ONLY)
	private String objectType;

	@Schema(description = "Number of matching objects of that type, across every page", examples = "12", accessMode = READ_ONLY)
	private Long count;

	public static ObjectTypeCount create() {
		return new ObjectTypeCount();
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	public ObjectTypeCount withObjectType(final String objectType) {
		this.objectType = objectType;
		return this;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(final Long count) {
		this.count = count;
	}

	public ObjectTypeCount withCount(final Long count) {
		this.count = count;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ObjectTypeCount that = (ObjectTypeCount) o;
		return Objects.equals(objectType, that.objectType) && Objects.equals(count, that.count);
	}

	@Override
	public int hashCode() {
		return Objects.hash(objectType, count);
	}

	@Override
	public String toString() {
		return "ObjectTypeCount{" +
			"objectType='" + objectType + '\'' +
			", count=" + count +
			'}';
	}
}
