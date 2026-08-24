package se.sundsvall.memories.api.model;

/**
 * What each search accepts under {@code sortBy}: the pattern its parameter class validates against, and the message
 * that names the alternatives when it does not match.
 *
 * <p>
 * A sort property feeds a specification, so it is an attribute of the entity rather than a column of the table. One
 * that is neither stays invisible until it reaches Spring Data, where it becomes a {@code 500}; validating against
 * these turns it into a {@code 400 Constraint Violation} that tells the caller what to ask for instead.
 *
 * <p>
 * The values live here rather than in each parameter class because an annotation needs them as compile-time constants,
 * and eleven copies of the same two declarations is what a reviewer has to read past to find the one that differs.
 * Keeping them together also makes the one question worth asking of them — does every search offer what a client
 * needs to sort on? — answerable from a single screen.
 */
final class SortableProperties {

	private SortableProperties() {}

	static final String AUDIO = "documentTitle|date|id";
	static final String AUDIO_MESSAGE = "must be one of: documentTitle, date, id";

	static final String CENSUS_RECORD = "lastName|firstName|birthYear";
	static final String CENSUS_RECORD_MESSAGE = "must be one of: lastName, firstName, birthYear";

	static final String COMBINED_OBJECT = "relevance|objectKey|title|year|objectType";
	static final String COMBINED_OBJECT_MESSAGE = "must be one of: relevance, objectKey, title, year, objectType";

	static final String FILM = "documentTitle|date|id";
	static final String FILM_MESSAGE = "must be one of: documentTitle, date, id";

	static final String LEGAL_ENTITY = "name|startDate|endDate|legalEntityId";
	static final String LEGAL_ENTITY_MESSAGE = "must be one of: name, startDate, endDate, legalEntityId";

	static final String NODE = "name|startYear|stopYear|sortOrder";
	static final String NODE_MESSAGE = "must be one of: name, startYear, stopYear, sortOrder";

	static final String PERSON = "lastName|firstName|birthDate|birthParish";
	static final String PERSON_MESSAGE = "must be one of: lastName, firstName, birthDate, birthParish";

	static final String PHOTO = "documentTitle|earliest|latest|objectType|id";
	static final String PHOTO_MESSAGE = "must be one of: documentTitle, earliest, latest, objectType, id";

	static final String PUBLICATION = "documentTitle|documentDate|date|id";
	static final String PUBLICATION_MESSAGE = "must be one of: documentTitle, documentDate, date, id";

	static final String SEAMAN = "lastName1|firstName|birthDate|birthParish|id";
	static final String SEAMAN_MESSAGE = "must be one of: lastName1, firstName, birthDate, birthParish, id";

	static final String TEXT = "documentTitle|documentDate|id";
	static final String TEXT_MESSAGE = "must be one of: documentTitle, documentDate, id";
}
