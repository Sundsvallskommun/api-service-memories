package se.sundsvall.memories.api.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class FilmTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays((long) (Math.random() * 10000)), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(Film.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var filmId = 1L;
		final var filnamn = "film001.mp4";
		final var filmObjFil = "/path/to/file.mp4";
		final var objtyp = "VIDEO";
		final var datum = "1985-06-15";
		final var doktitel = "Midsommarfirande i Sundsvall";
		final var filmTId = 2L;
		final var filmOplats = "Sundsvall";
		final var filmOId = 3L;
		final var filmUEId = 4L;
		final var filmUId = 5L;
		final var kommentFilm = "En film om midsommarfirande";
		final var filmMimeType = "video/mp4";
		final var asv = "ASV001";
		final var nodeId = 6L;
		final var options = 0L;
		final var deletedDate = LocalDate.of(2026, 1, 15);

		final var result = Film.create()
			.withFilmId(filmId)
			.withFilnamn(filnamn)
			.withFilmObjFil(filmObjFil)
			.withObjtyp(objtyp)
			.withDatum(datum)
			.withDoktitel(doktitel)
			.withFilmTId(filmTId)
			.withFilmOplats(filmOplats)
			.withFilmOId(filmOId)
			.withFilmUEId(filmUEId)
			.withFilmUId(filmUId)
			.withKommentFilm(kommentFilm)
			.withFilmMimeType(filmMimeType)
			.withAsv(asv)
			.withNodeId(nodeId)
			.withOptions(options)
			.withDeletedDate(deletedDate);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getFilmId()).isEqualTo(filmId);
		assertThat(result.getFilnamn()).isEqualTo(filnamn);
		assertThat(result.getFilmObjFil()).isEqualTo(filmObjFil);
		assertThat(result.getObjtyp()).isEqualTo(objtyp);
		assertThat(result.getDatum()).isEqualTo(datum);
		assertThat(result.getDoktitel()).isEqualTo(doktitel);
		assertThat(result.getFilmTId()).isEqualTo(filmTId);
		assertThat(result.getFilmOplats()).isEqualTo(filmOplats);
		assertThat(result.getFilmOId()).isEqualTo(filmOId);
		assertThat(result.getFilmUEId()).isEqualTo(filmUEId);
		assertThat(result.getFilmUId()).isEqualTo(filmUId);
		assertThat(result.getKommentFilm()).isEqualTo(kommentFilm);
		assertThat(result.getFilmMimeType()).isEqualTo(filmMimeType);
		assertThat(result.getAsv()).isEqualTo(asv);
		assertThat(result.getNodeId()).isEqualTo(nodeId);
		assertThat(result.getOptions()).isEqualTo(options);
		assertThat(result.getDeletedDate()).isEqualTo(deletedDate);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Film.create()).hasAllNullFieldsOrProperties();
	}
}
