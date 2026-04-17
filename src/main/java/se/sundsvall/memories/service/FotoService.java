package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.Foto;
import se.sundsvall.memories.api.model.FotoParameters;
import se.sundsvall.memories.api.model.PagedFotoResponse;
import se.sundsvall.memories.integration.db.FotoRepository;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.model.FotoEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.FotoMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
public class FotoService {

	private final FotoRepository fotoRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;

	public FotoService(final FotoRepository fotoRepository,
		final SambaIntegration sambaIntegration, final SambaIntegrationProperties sambaProperties) {
		this.fotoRepository = fotoRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
	}

	public PagedFotoResponse search(final FotoParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());
		final var sanitized = FulltextQuery.sanitize(parameters.getQuery());

		final var page = sanitized == null
			? fotoRepository.findAllPublished(pageable)
			: fotoRepository.searchPublished(sanitized, pageable);

		return PagedFotoResponse.create()
			.withPhotos(FotoMapper.toFotoList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}

	public Foto getById(final Integer id) {
		return fotoRepository.findById(id)
			.map(FotoMapper::toFoto)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Photo with id '%s' not found".formatted(id)));
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = fotoRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Photo with id '%s' not found".formatted(id)));

		final var filename = ofNullable(variant.extract(entity))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Photo with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		response.addHeader(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE);
		response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(filename));

		streamFileContent(id, variant, filename, response);
	}

	private void streamFileContent(final Integer id, final FileVariant variant, final String filename, final HttpServletResponse response) {
		final var path = sambaProperties.fotoFolder() + variant.getSubfolder() + "/" + filename;
		try {
			sambaIntegration.streamFile(path, response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for photo with id '%s': %s".formatted(id, e.getMessage()));
		}
	}

	public enum FileVariant {
		LITEN("fil_liten", FotoEntity::getFilLiten),
		STOR("fil_stor", FotoEntity::getFilStor),
		ORIGINAL("fil_original", FotoEntity::getFilOriginal);

		private final String subfolder;
		private final Function<FotoEntity, String> fileNameExtractor;

		FileVariant(final String subfolder, final Function<FotoEntity, String> fileNameExtractor) {
			this.subfolder = subfolder;
			this.fileNameExtractor = fileNameExtractor;
		}

		String extract(final FotoEntity entity) {
			return fileNameExtractor.apply(entity);
		}

		String getSubfolder() {
			return subfolder;
		}
	}
}
