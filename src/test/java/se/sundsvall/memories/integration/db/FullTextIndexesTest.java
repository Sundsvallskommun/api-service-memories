package se.sundsvall.memories.integration.db;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.memories.Application;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The half that decides whether a search may use {@code MATCH} at all. Run against the real schema, since the point of
 * the class is to describe the database rather than a fixture.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
@ExtendWith(MockitoExtension.class)
class FullTextIndexesTest {

	@Test
	void coversTheIndexesTheSearchesDependOn() {
		assertThat(FullTextIndexes.covers("FOTO", List.of("DOKTITEL", "KOMMENT_FF"))).isTrue();
		assertThat(FullTextIndexes.covers("PUBL", List.of("DOKTITEL", "KOMMENT_PUBL", "XMLTEXT"))).isTrue();
		assertThat(FullTextIndexes.covers("TEXT", List.of("DOKTITEL", "KOMMENT_DOC", "XMLTEXT"))).isTrue();
		assertThat(FullTextIndexes.covers("TBL_NODES", List.of("NAME", "DESCRIPTION"))).isTrue();
	}

	@Test
	void pairsWithAnIndexByColumnSetRatherThanByDeclaredOrder() {
		assertThat(FullTextIndexes.covers("PUBL", List.of("XMLTEXT", "DOKTITEL", "KOMMENT_PUBL"))).isTrue();
		assertThat(FullTextIndexes.covers("publ", List.of("doktitel", "komment_publ", "xmltext"))).isTrue();
	}

	/**
	 * The failure this class exists to prevent: a column list MariaDB cannot pair with an index is error 1191, so the
	 * caller has to be told no and use {@code LIKE}. A partial list is as unusable as a missing table.
	 */
	@Test
	void doesNotCoverAPartialListOrATableWithoutTheIndex() {
		assertThat(FullTextIndexes.covers("PUBL", List.of("DOKTITEL", "KOMMENT_PUBL"))).isFalse();
		assertThat(FullTextIndexes.covers("TBL_NODES", List.of("NAME"))).isFalse();
		assertThat(FullTextIndexes.covers("MANTAL", List.of("MNMNF", "MNMNE"))).isFalse();
		assertThat(FullTextIndexes.covers("NO_SUCH_TABLE", List.of("NOPE"))).isFalse();
	}
}
