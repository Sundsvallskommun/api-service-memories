package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Entity for the {@code INSTITUTION} lookup table — the institution (arkivinstitution) that holds an archive or
 * collection, reached from a node through {@code TBL_NODEATTRIBUTES.FIELD3}. The legacy table carries some forty
 * contact columns per area of responsibility; only the ones the API shows are mapped.
 */
@Entity
@Table(name = "INSTITUTION")
public class InstitutionEntity {

	@Id
	@Column(name = "I_ID")
	private Integer id;

	@Column(name = "INSTNAMN", length = 60)
	private String name;

	@Column(name = "INSTKOD", length = 10)
	private String code;

	@Column(name = "BESKRIVNING", length = 256)
	private String description;

	@Column(name = "URL", length = 200)
	private String url;

	@Column(name = "EPOST", length = 200)
	private String email;

	public static InstitutionEntity create() {
		return new InstitutionEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public InstitutionEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public InstitutionEntity withName(final String name) {
		this.name = name;
		return this;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public InstitutionEntity withCode(final String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public InstitutionEntity withDescription(final String description) {
		this.description = description;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public InstitutionEntity withUrl(final String url) {
		this.url = url;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public InstitutionEntity withEmail(final String email) {
		this.email = email;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final InstitutionEntity that = (InstitutionEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(code, that.code) && Objects.equals(description, that.description)
			&& Objects.equals(url, that.url) && Objects.equals(email, that.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, code, description, url, email);
	}

	@Override
	public String toString() {
		return "InstitutionEntity{" +
			"id=" + id +
			", name='" + name + '\'' +
			", code='" + code + '\'' +
			", description='" + description + '\'' +
			", url='" + url + '\'' +
			", email='" + email + '\'' +
			'}';
	}
}
