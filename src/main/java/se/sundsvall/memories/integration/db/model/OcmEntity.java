package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "OCM")
public class OcmEntity {

	@Id
	@Column(name = "O_ID")
	private Integer id;

	@Column(name = "OCMTEXT", length = 200)
	private String text;

	@Column(name = "OCMKOD", length = 20)
	private String code;

	@Column(name = "OCMDESC", length = 500)
	private String description;

	public static OcmEntity create() {
		return new OcmEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public OcmEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public OcmEntity withText(final String text) {
		this.text = text;
		return this;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public OcmEntity withCode(final String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public OcmEntity withDescription(final String description) {
		this.description = description;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final OcmEntity that = (OcmEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(text, that.text) && Objects.equals(code, that.code) && Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, text, code, description);
	}

	@Override
	public String toString() {
		return "OcmEntity{" +
			"id=" + id +
			", text='" + text + '\'' +
			", code='" + code + '\'' +
			", description='" + description + '\'' +
			'}';
	}
}
