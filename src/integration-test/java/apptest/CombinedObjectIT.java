package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.memories.Application;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@WireMockAppTestSuite(files = "classpath:/CombinedObjectIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class CombinedObjectIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String RESPONSE_FILE = "response.json";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/objects";

	@Test
	void test01_searchObjectsByQuery() {
		setupCall()
			.withServicePath(PATH + "?query=Folkmusik")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The register types (Person, Sjöman) take part in the same union as the object types — a name matches the person
	 * record as well as both seamen, including the one carrying the name in its second surname column.
	 */
	@Test
	void test02_searchObjectsIncludesRegisters() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A legal entity is the one register type with a topography reference, so its location is resolved to a place name
	 * just like the object types', and its year comes from the start of the activity period.
	 */
	@Test
	void test03_searchObjectsIncludesLegalEntities() {
		setupCall()
			.withServicePath(PATH + "?query=kommitt")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Unpublished register rows (person 3 and legal entity 30, neither with bit 4 of OPTIONS set) must stay out of the
	 * combined search, exactly as they stay out of the per-type searches.
	 */
	@Test
	void test04_searchObjectsExcludesUnpublishedRegisterRows() {
		setupCall()
			.withServicePath(PATH + "?query=Dold")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The placeholder rows PERSON.P_ID = 0 and JURPERS.J_ID = 1 ("ingen") are sentinels, not archive records. Both are
	 * flagged published, so only the explicit id predicates keep them out of the result.
	 */
	@Test
	void test05_searchObjectsExcludesRegisterPlaceholders() {
		setupCall()
			.withServicePath(PATH + "?query=Ingen")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Every value the parameter accepts has to resolve as an entity attribute. A property that only fails once it
	 * reaches Spring Data is a 500 no unit test would catch, so each one is walked through the whole request here.
	 */
	@Test
	void test06_searchObjectsSortedByTitle() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=title&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_searchObjectsSortedByYear() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=year&sortBy=objectKey&sortDirection=DESC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_searchObjectsSortedByObjectType() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=objectType&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A column of the view is not an entity attribute, and is refused with the list of alternatives rather than
	 * reaching Spring Data and failing there.
	 */
	@Test
	void test09_searchObjectsWithInvalidSortBy() {
		setupCall()
			.withServicePath(PATH + "?sortBy=SORT_YEAR")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Only the object branches of the view carry an originator, so filtering on one also leaves out the register types
	 * — a person is not created by anyone. Film 1 is the one row with a real originator.
	 */
	@Test
	void test10_searchObjectsByCreator() {
		setupCall()
			.withServicePath(PATH + "?creator=Nordin")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The placeholder rows both answer to "Ingen", and nearly every object points at them. Searching for that word
	 * must not return the whole archive.
	 */
	@Test
	void test11_searchObjectsByCreatorIgnoresThePlaceholders() {
		setupCall()
			.withServicePath(PATH + "?creator=Ingen")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A known person's name puts the person first. Photo 1005 mentions the same name in its comment and comes last:
	 * before the ranking existed the two were indistinguishable, and a name that occurred in many comments buried the
	 * one record that actually carried it. Seaman 1 is the same person in the seamen's register and ranks between them,
	 * on the strength of its name rather than its comment.
	 */
	@Test
	void test12_searchObjectsRanksTheNamedPersonFirst() {
		setupCall()
			.withServicePath(PATH + "?query=Anton Nordin")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A company name matches the legal entity record. Legal entity 10 is registered as "Nödhjälpskommittén 1888-1889"
	 * and known as "Nödhjälpskommittén", which is an alternative name the view now ranks on — the photo that merely
	 * credits the company in its comment follows it.
	 */
	@Test
	void test13_searchObjectsMatchesLegalEntityByCompanyName() {
		setupCall()
			.withServicePath(PATH + "?query=Nödhjälpskommittén")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * A document title matches the document that carries it, ahead of the one that only mentions it.
	 */
	@Test
	void test14_searchObjectsMatchesTheDocumentTitle() {
		setupCall()
			.withServicePath(PATH + "?query=Stadsvy")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Every word has to occur, but not as a phrase and not in the same column: the seaman register stores this name
	 * across a first name and a second surname, and the whole-string match this replaces found nothing at all for it.
	 */
	@Test
	void test15_searchObjectsMatchesASecondSurname() {
		setupCall()
			.withServicePath(PATH + "?query=Erik Nordin")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Relevance is also a sort the caller can ask for by name, so a client that has sorted by something else can get
	 * back to it — and asking for it explicitly is reported back under {@code _meta.sortBy}, unlike the default.
	 */
	@Test
	void test16_searchObjectsSortedByRelevance() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&sortBy=relevance")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Deletion sets DELETEDDATE but leaves the published bit set. The view checked only the bit, so every soft-deleted
	 * row — a film, a photo, an audio, a text and a publication in this data — stayed findable here long after the
	 * per-type searches had stopped returning them.
	 */
	@Test
	void test17_searchObjectsExcludesSoftDeletedRows() {
		setupCall()
			.withServicePath(PATH + "?query=raderad")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The type selection narrows the list to the chosen types, and deliberately leaves the counters alone: the chips
	 * still count Foto and Person, so the client that rendered them can tell the user what selecting those would give
	 * and can offer a way back. Every other filter — here the query — still narrows both.
	 */
	@Test
	void test18_searchObjectsFilteredByObjectType() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&objectType=Sjöman&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * Several types are alternatives rather than further restrictions, which is the whole point of filtering here
	 * rather than calling one per-type endpoint per chip and merging the pages in a client: the result is one list,
	 * sorted and paged across every selected type at once.
	 */
	@Test
	void test19_searchObjectsFilteredBySeveralObjectTypes() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&objectType=Person,Sjöman&sortBy=objectKey&sortDirection=ASC")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	/**
	 * The set of types is the archive's rather than this API's — FOTO carries its own OBJTYP — so an unknown one is
	 * not a rejected request but an empty result, with the counters left to say which types there were.
	 */
	@Test
	void test20_searchObjectsFilteredByUnknownObjectType() {
		setupCall()
			.withServicePath(PATH + "?query=Nordin&objectType=Karta")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	// ---------------------------------------------------------------------------------------------------------------
	// Searches from the business
	//
	// Tests 12 to 14 cover the three acceptance criteria with names taken from this suite's own test data — a person,
	// a company and a document title that happen to exist here. They prove the ranking works; they do not prove it
	// works on the searches people actually make.
	//
	// The remaining acceptance criterion is that the test cases are verified against the business's examples. Add one
	// test per example below, with the search term Lena and Niklas give and the record they expect at the top, and add
	// the matching rows to testdata-it.sql. A real example that ranks the wrong record first is the finding this whole
	// story exists to surface, so it belongs here as a failing test rather than in a comment.
	// ---------------------------------------------------------------------------------------------------------------
}
