package se.sundsvall.memories.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.memories.api.model.Institution;
import se.sundsvall.memories.integration.db.InstitutionRepository;
import se.sundsvall.memories.integration.db.model.InstitutionEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

	@Mock
	private InstitutionRepository repositoryMock;

	@InjectMocks
	private InstitutionService service;

	@Test
	void getInstitutionsMapsEveryInstitution() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of(
			InstitutionEntity.create().withId(2).withName("Föreningsarkivet Västernorrland").withCode("FAV"),
			InstitutionEntity.create().withId(3).withName("Sundsvalls museum").withCode("SVM")));

		final var result = service.getInstitutions();

		assertThat(result).extracting(Institution::getInstitutionId, Institution::getName, Institution::getCode)
			.containsExactly(tuple(2, "Föreningsarkivet Västernorrland", "FAV"), tuple(3, "Sundsvalls museum", "SVM"));
		verify(repositoryMock).findAllSelectable();
	}

	/**
	 * Swedish order, not the database's: Ö comes after Z, which a {@code general_ci} collation would not give.
	 * Institutions sharing a name keep a stable order, by id.
	 */
	@Test
	void getInstitutionsSortsByNameInSwedishOrder() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of(
			InstitutionEntity.create().withId(1).withName("Örnsköldsviks museum"),
			InstitutionEntity.create().withId(2).withName("Sundsvalls museum"),
			InstitutionEntity.create().withId(3).withName("Ånge kommunarkiv"),
			InstitutionEntity.create().withId(9).withName("Sundsvalls museum"),
			InstitutionEntity.create().withId(4).withName("Föreningsarkivet Västernorrland")));

		assertThat(service.getInstitutions()).extracting(Institution::getInstitutionId, Institution::getName)
			.containsExactly(
				tuple(4, "Föreningsarkivet Västernorrland"), tuple(2, "Sundsvalls museum"), tuple(9, "Sundsvalls museum"),
				tuple(3, "Ånge kommunarkiv"), tuple(1, "Örnsköldsviks museum"));
	}

	@Test
	void getInstitutionsWhenThereAreNone() {
		when(repositoryMock.findAllSelectable()).thenReturn(List.of());

		assertThat(service.getInstitutions()).isEmpty();
		verify(repositoryMock).findAllSelectable();
	}
}
