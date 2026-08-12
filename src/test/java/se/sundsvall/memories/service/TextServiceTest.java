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
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.integration.db.TextMediaRepository;
import se.sundsvall.memories.integration.db.TextRepository;
import se.sundsvall.memories.integration.db.model.TextEntity;
import se.sundsvall.memories.integration.db.model.TextMediaEntity;
import se.sundsvall.memories.integration.db.model.TopographyEntity;
import se.sundsvall.memories.service.TextService.FileVariant;
import se.sundsvall.memories.service.TextService.MediaFileVariant;
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
class TextServiceTest {

	private static final String STREAM_ERROR_CONTEXT = "IOException occurred when streaming file for text with id '1001'";

	private static final String MEDIA_STREAM_ERROR_CONTEXT = "IOException occurred when streaming media file '1' for text with id '1001'";

	@Mock
	private TextRepository textRepositoryMock;

	@Mock
	private TextMediaRepository textMediaRepositoryMock;

	@Mock
	private OcmLookup ocmLookupMock;

	@Mock
	private FileStreamer fileStreamerMock;

	private TextService service;

	private static TextEntity entity() {
		return TextEntity.create()
			.withTextId(1001)
			.withDocumentTitle("Minne från Sundsvall")
			.withTopography(TopographyEntity.create().withTId(4).withName("Sundsvalls kommun"))
			.withSubjectId(20)
			.withThumbnailFilename("TEXT.id_1001_fil_liten.jpeg")
			.withLargeImageFilename("TEXT.id_1001_fil_stor.jpeg")
			.withOcrFilename("TEXT.id_1001_fil_txt.xml")
			.withXmltext("<text>content</text>")
			.withOptions(4);
	}

	static Stream<Arguments> fileVariants() {
		return Stream.of(
			Arguments.of(FileVariant.THUMBNAIL, "/text/fil_liten/TEXT.id_1001_fil_liten.jpeg", "TEXT.id_1001_fil_liten.jpeg", false),
			Arguments.of(FileVariant.LARGE, "/text/fil_stor/TEXT.id_1001_fil_stor.jpeg", "TEXT.id_1001_fil_stor.jpeg", false),
			Arguments.of(FileVariant.TEXT, "/text/fil_txt/TEXT.id_1001_fil_txt.xml", "TEXT.id_1001_fil_txt.xml", true));
	}

	private static TextMediaEntity mediaEntity() {
		return TextMediaEntity.create()
			.withTextId(1001)
			.withId(1)
			.withThumbnailFilename("TEXT.id_1001.multi_1.fil_liten.jpeg")
			.withLargeImageFilename("TEXT.id_1001.multi_1.fil_stor.jpeg")
			.withOriginalFilename("TEXT.id_1001.multi_1.fil_original.jpeg");
	}

	static Stream<Arguments> mediaFileVariants() {
		return Stream.of(
			Arguments.of(MediaFileVariant.THUMBNAIL, "/text_multi/fil_liten/TEXT.id_1001.multi_1.fil_liten.jpeg", "TEXT.id_1001.multi_1.fil_liten.jpeg"),
			Arguments.of(MediaFileVariant.LARGE, "/text_multi/fil_stor/TEXT.id_1001.multi_1.fil_stor.jpeg", "TEXT.id_1001.multi_1.fil_stor.jpeg"),
			Arguments.of(MediaFileVariant.ORIGINAL, "/text_multi/fil_original/TEXT.id_1001.multi_1.fil_original.jpeg", "TEXT.id_1001.multi_1.fil_original.jpeg"));
	}

	@BeforeEach
	void setUp() {
		service = new TextService(textRepositoryMock, textMediaRepositoryMock, SAMBA_PROPERTIES, ocmLookupMock, fileStreamerMock);
	}

	// Which rows the filters select is verified against a real database in TextSpecificationTest. These tests cover
	// what the service itself does: build the pageable, forward the parameters, and map the resulting page.

	@Test
	void searchDelegatesToRepositoryAndMapsThePage() {
		final var pageable = PageRequest.of(0, 100);
		when(textRepositoryMock.findAllByParameters(any(TextParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity()), pageable, 1));

		final var result = service.search(TextParameters.create().withQuery("stadshuset"));

		assertThat(result.getTexts()).hasSize(1);
		assertThat(result.getTexts().getFirst().getDocumentTitle()).isEqualTo("Minne från Sundsvall");
		assertThat(result.getTexts().getFirst().getXmltext()).isNull();
		assertThat(result.getTexts().getFirst().getMediaFiles()).isNull();
		verify(textRepositoryMock).findAllByParameters(any(TextParameters.class), eq(pageable));
		verifyNoMoreInteractions(textRepositoryMock);
	}

	@Test
	void searchForwardsTheParametersUnchanged() {
		final var pageable = PageRequest.of(0, 100);
		final var parameters = TextParameters.create();
		when(textRepositoryMock.findAllByParameters(any(TextParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		final var result = service.search(parameters);

		assertThat(result.getTexts()).isEmpty();
		// Which filters the parameters turn into is the repository's job, verified against a real database in
		// TextSpecificationTest. The service only has to hand them over untouched.
		final var parametersCaptor = ArgumentCaptor.forClass(TextParameters.class);
		verify(textRepositoryMock).findAllByParameters(parametersCaptor.capture(), eq(pageable));
		assertThat(parametersCaptor.getValue()).isSameAs(parameters);
		verifyNoMoreInteractions(textRepositoryMock);
	}

	@Test
	void searchAppliesRequestedPaging() {
		final var pageable = PageRequest.of(2, 25);
		when(textRepositoryMock.findAllByParameters(any(TextParameters.class), eq(pageable)))
			.thenReturn(new PageImpl<>(List.of(entity()), pageable, 51));

		final var result = service.search(TextParameters.create().withPage(3).withLimit(25));

		assertThat(result.getMetaData().getPage()).isEqualTo(3);
		assertThat(result.getMetaData().getLimit()).isEqualTo(25);
		assertThat(result.getMetaData().getTotalRecords()).isEqualTo(51);
		verify(textRepositoryMock).findAllByParameters(any(TextParameters.class), eq(pageable));
		verifyNoMoreInteractions(textRepositoryMock);
	}

	@Test
	void getByIdIncludesXmltextAndMediaFiles() {
		when(textRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entity()));
		when(textMediaRepositoryMock.findByTextIdOrderById(1001)).thenReturn(List.of(
			TextMediaEntity.create().withTextId(1001).withThumbnailFilename("extra-liten.jpg")));

		when(ocmLookupMock.resolve(20)).thenReturn("Musik");

		final var result = service.getById(1001);

		assertThat(result).isNotNull();
		assertThat(result.getTextId()).isEqualTo(1001);
		assertThat(result.getXmltext()).isEqualTo("<text>content</text>");
		assertThat(result.getLocation()).isEqualTo("Sundsvalls kommun");
		assertThat(result.getSubject()).isEqualTo("Musik");
		assertThat(result.getMediaFiles()).hasSize(1);
		assertThat(result.getMediaFiles().getFirst().getThumbnailFilename()).isEqualTo("extra-liten.jpg");
	}

	@Test
	void getByIdNotFound() {
		when(textRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getById(999));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Text with id '999' not found");
	}

	@ParameterizedTest
	@MethodSource("fileVariants")
	void streamFileDelegatesToFileStreamer(final FileVariant variant, final String expectedPath, final String expectedFilename, final boolean expectedTransform) {
		final var responseMock = mock(HttpServletResponse.class);
		when(textRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entity()));

		service.streamFile(1001, variant, responseMock);

		verify(fileStreamerMock).streamInline(expectedPath, expectedFilename, expectedTransform, responseMock, STREAM_ERROR_CONTEXT);
	}

	@Test
	void streamFileNotFound() {
		final var responseMock = mock(HttpServletResponse.class);
		when(textRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(999, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Text with id '999' not found");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withOcrFilename("   ");
		when(textRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1001, FileVariant.TEXT, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'text'");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamFileWhenVariantIsNull() {
		final var responseMock = mock(HttpServletResponse.class);
		final var entityMissingFile = entity().withThumbnailFilename(null);
		when(textRepositoryMock.findVisibleById(anyInt())).thenReturn(Optional.of(entityMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamFile(1001, FileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("no file for variant 'thumbnail'");
		verifyNoInteractions(fileStreamerMock);
	}

	@ParameterizedTest
	@MethodSource("mediaFileVariants")
	void streamMediaFileDelegatesToFileStreamer(final MediaFileVariant variant, final String expectedPath, final String expectedFilename) {
		final var responseMock = mock(HttpServletResponse.class);
		when(textMediaRepositoryMock.findById(new TextMediaEntity.TextMediaId(1001, 1))).thenReturn(Optional.of(mediaEntity()));

		service.streamMediaFile(1001, 1, variant, responseMock);

		verify(fileStreamerMock).streamInline(expectedPath, expectedFilename, false, responseMock, MEDIA_STREAM_ERROR_CONTEXT);
	}

	@Test
	void streamMediaFileNotFound() {
		final var responseMock = mock(HttpServletResponse.class);
		when(textMediaRepositoryMock.findById(new TextMediaEntity.TextMediaId(1001, 99))).thenReturn(Optional.empty());

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamMediaFile(1001, 99, MediaFileVariant.THUMBNAIL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("Media file with id '99' for text with id '1001' not found");
		verifyNoInteractions(fileStreamerMock);
	}

	@Test
	void streamMediaFileWhenVariantIsBlank() {
		final var responseMock = mock(HttpServletResponse.class);
		final var mediaMissingFile = mediaEntity().withOriginalFilename("   ");
		when(textMediaRepositoryMock.findById(new TextMediaEntity.TextMediaId(1001, 1))).thenReturn(Optional.of(mediaMissingFile));

		final var exception = assertThrows(ThrowableProblem.class,
			() -> service.streamMediaFile(1001, 1, MediaFileVariant.ORIGINAL, responseMock));

		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).contains("has no file for variant 'original'");
		verifyNoInteractions(fileStreamerMock);
	}
}
