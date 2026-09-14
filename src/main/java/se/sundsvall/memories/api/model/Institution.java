package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Institution (arkivinstitution) model — the institution that holds an archive or collection")
public class Institution {

	@Schema(description = "Institution ID", examples = "3")
	private Integer institutionId;

	@Schema(description = "Institution code (institutionskod)", examples = "SVM")
	private String code;

	@Schema(description = "Institution name", examples = "Sundsvalls museum")
	private String name;

	@Schema(description = "Description of the organisation", examples = "Kommunalt museum med arkiv och samlingar")
	private String description;

	@Schema(description = "Web address", examples = "https://sundsvallsmuseum.se")
	private String url;

	@Schema(description = "E-mail address", examples = "museet@sundsvall.se")
	private String email;

	public static Institution create() {
		return new Institution();
	}

	public Integer getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(final Integer institutionId) {
		this.institutionId = institutionId;
	}

	public Institution withInstitutionId(final Integer institutionId) {
		this.institutionId = institutionId;
		return this;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public Institution withCode(final String code) {
		this.code = code;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Institution withName(final String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Institution withDescription(final String description) {
		this.description = description;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public Institution withUrl(final String url) {
		this.url = url;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public Institution withEmail(final String email) {
		this.email = email;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Institution that = (Institution) o;
		return Objects.equals(institutionId, that.institutionId) && Objects.equals(code, that.code) && Objects.equals(name, that.name)
			&& Objects.equals(description, that.description) && Objects.equals(url, that.url) && Objects.equals(email, that.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(institutionId, code, name, description, url, email);
	}

	@Override
	public String toString() {
		return "Institution{" +
			"institutionId=" + institutionId +
			", code='" + code + '\'' +
			", name='" + name + '\'' +
			", description='" + description + '\'' +
			", url='" + url + '\'' +
			", email='" + email + '\'' +
			'}';
	}
}
