package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Subject / Ämne (OCM)")
public class Subject {

	@Schema(description = "Subject code (OCMKOD)", examples = "ALM")
	private String code;

	@Schema(description = "Subject label (OCMTEXT)", examples = "Allmänt")
	private String text;

	@Schema(description = "Subject description (OCMDESC)", examples = "Allmänt ämne")
	private String description;

	public static Subject create() {
		return new Subject();
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public Subject withCode(final String code) {
		this.code = code;
		return this;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public Subject withText(final String text) {
		this.text = text;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Subject withDescription(final String description) {
		this.description = description;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Subject that = (Subject) o;
		return Objects.equals(code, that.code) && Objects.equals(text, that.text) && Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, text, description);
	}

	@Override
	public String toString() {
		return "Subject{" +
			"code='" + code + '\'' +
			", text='" + text + '\'' +
			", description='" + description + '\'' +
			'}';
	}
}
