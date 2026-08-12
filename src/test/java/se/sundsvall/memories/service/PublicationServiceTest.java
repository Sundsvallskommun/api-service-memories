package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.integration.db.PublicationRepository;
import se.sundsvall.memories.integration.db.model.PublicationEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;
import se.sundsvall.memories.service.PublicationService.FileVariant;
import se.sundsvall.memories.service.util.FileStreamer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.memories.integration.samba.SambaTestProperties.SAMBA_PROPERTIES;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

	private static final String STREAM_ERROR_CONTEXT = "IOException occurred when streaming file for publication with id '207'";

	@Mock
	private PublicationRepository publicationRepositoryMock;

	@Mock
	private FileStreamer fileStreamerMock;

	private PublicationService service;

	private static PublicationEntity entity() {
		return PublicationEntity.create()
			.withPublicationId(207)
			.withDocumentTitle("Alfwar och Skämt")
			.withTopography(TopographyEntity.create().withTId(4).withName("Sundsvall"))
			.withPublicationType("Broschyrer")
			.withThumbnailFilename("PUBL.id_207_fil_liten.jpeg")
			.withLargeImageFilename("PUBL.id_207_fil_stor.jpeg")
			.withOcrFilename("PUBL.id_207_fil_txt.xml")
			.withXmltext("<text>content</text>")
			.withOptions(4);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.THUMBNAIL, "/publ/fil_liten/PUBL.id_207_fil_liten.jpeg", "PUBL.id_207_fil_liten.jpeg", false),
			Arguments.of(FileVariant.LARGE, "/publ/fil_stor/PUBL.id_207_fil_stor.jpeg", "PUBL.id_207_fil_stor.jpeg", false),
			Arguments.of(FileVariant.TEXT, "/publ/fil_txt/PUBL.id_207_fil_txt.xml", "PUBL.id_207_fil_txt.xml", true));
	}

	@BeforeEach
	void setUp() {
		service = new PublicationService(publicationRepositoryMock, SAMBA_PROPERTIES, fileStreamerMock);
	}

	// Which rows the filters select is verified against a real database in PublicationSpecificationTest. These tests
	// cover what the service itself does: build the pageable, forward the parameters, and map the resulting page.

	@Test
	void searchDelegatesToRepositoryAndMapsThePage() {
		final var pageable = PageRequest.of(0, 100);
		when(publicationRepositoryMock.findAllByParameters(any(PublicationParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(PublicationParameters.create().withQuery("Drowning"));

		assertThat(result.getPublications()).hasSize(1);
		assertThat(result.getPublications().getFirst().getPublicationType()).isEqualTo("Broschyrer");
		assertThat(result.getPublications().getFirst().getLocation()).isEqualTo("Sundsvall");
		assertThat(result.getPublications().getFirst().getXmltext()).isNull();
		assertThat(result.getMetaData().getPage()).isEqualTo(1);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(1);
		verify(publicationRepositoryMock).findAllByParameters(any(PublicationParameters.class), eq(pageable));
		verifyNoMoreInteractions(publicationRepositoryMock);
	}

	@Test
	void searchForwardsTheParametersUnchanged() {
		final var pageable = PageRequest.of(0, 100);
		final var parameters = PublicationParameters.create();
		when(publicationRepositoryMock.findAllByParameters(any(PublicationParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(parameters);

		assertThat(result.getPublications()).isEmpty();
		final var parametersCaptor = ArgumentCaptor.forClass(PublicationParameters.class);
		verify(publicationRepositoryMock).findAllByParameters(parametersCaptor.capture(), eq(pageable));
		assertThat(parametersCaptor.getValue()).isSameAs(parameters);
		verifyNoMoreInteractions(publicationRepositoryMock);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25);
		when(publicationRepositoryMock.findAllByParameters(any(PublicationParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity()), pageable, 51));

		final var result = service.search(PublicationParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
		verify(publicationRepositoryMock).findAllByParameters(any(PublicationParameters.class), eq(pageable));
		verifyNoMoreInteractions(publicationRepositoryMock);
	}

	@Test
	void getByIdIncludesXmltextAndLocation() {
		when(publicationRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entity()));

		final var result = service.getById(207);

		assertThat(result).isNotNull();
		assertThat(result.getPublicationId()).isEqualTo(207);
		assertThat(result.getXmltext()).isEqualTo("<text>content</text>");
		assertThat(result.getPublicationType()).isEqualTo("Broschyrer");
		assertThat(result.getLocation()).isEqualTo("Sundsvall");
	}

	@Test
	void getByIdNotFound() {
		when(publicationRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Publication with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void streamFileDelegatesToFileStreamer(final FileVariant variant, final String expectedPath, final String expectedFilename, final boolean expectedTransform) {
		final var responseMock = mock(HttpServletResponse.class);
		when(publicationRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entity()));

		service.streamFile(207, variant, responseMock);

		verify(fileStreamerMock).streamInline(expectedPath, expectedFilename, expectedTransform, responseMock, STREAM_ERROR_CONTEXT);
	}

	@Test
	void streamFileNotFoundPublication() {
		final var responseMock = mock(HttpServletResponse.class);
		when(publicationRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(999, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Publication with id '999' not found");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withOcrFilename("   ");
		when(publicationRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.TEXT, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'text'");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamFileWhenVariantIsNull() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withThumbnailFilename(null);
		when(publicationRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(207, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'thumbnail'");
		verifyNoInteractions(fileStreamerMock);
	}
}
