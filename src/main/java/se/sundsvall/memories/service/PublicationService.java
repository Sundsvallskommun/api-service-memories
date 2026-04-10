package se.sundsvall.memories.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.integration.db.FulltextQuery;
import se.sundsvall.memories.integration.db.PublRepository;
import se.sundsvall.memories.integration.db.PublTypRepository;
import se.sundsvall.memories.integration.db.model.PublEntity;
import se.sundsvall.memories.integration.db.model.PublTypEntity;
import se.sundsvall.memories.integration.samba.SambaIntegration;
import se.sundsvall.memories.integration.samba.SambaIntegrationProperties;
import se.sundsvall.memories.service.mapper.PublicationMapper;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
public class PublicationService {

	private final PublRepository publRepository;
	private final PublTypRepository publTypRepository;
	private final SambaIntegration sambaIntegration;
	private final SambaIntegrationProperties sambaProperties;

	public PublicationService(final PublRepository publRepository, final PublTypRepository publTypRepository,
		final SambaIntegration sambaIntegration, final SambaIntegrationProperties sambaProperties) {
		this.publRepository = publRepository;
		this.publTypRepository = publTypRepository;
		this.sambaIntegration = sambaIntegration;
		this.sambaProperties = sambaProperties;
	}

	public List<Publication> search(final String query) {
		final var sanitized = FulltextQuery.sanitize(query);
		final var entities = sanitized == null
			? publRepository.findAllPublished()
			: publRepository.searchPublished(sanitized);

		return PublicationMapper.toPublicationList(entities, loadPubliktypLookup());
	}

	public Publication getById(final Integer id) {
		final var lookup = loadPubliktypLookup();
		return publRepository.findById(id)
			.map(entity -> PublicationMapper.toPublication(entity, lookup))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Publication with id '%s' not found".formatted(id)));
	}

	public void streamFile(final Integer id, final FileVariant variant, final HttpServletResponse response) {
		final var entity = publRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "Publication with id '%s' not found".formatted(id)));

		final var filename = ofNullable(variant.extract(entity))
			.filter(name -> !name.isBlank())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND,
				"Publication with id '%s' has no file for variant '%s'".formatted(id, variant.name().toLowerCase())));

		response.addHeader(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE);
		response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(filename));

		streamFileContent(id, variant, filename, response);
	}

	private void streamFileContent(final Integer id, final FileVariant variant, final String filename, final HttpServletResponse response) {
		final var path = sambaProperties.publFolder() + variant.getSubfolder() + "/" + filename;
		try {
			sambaIntegration.streamFile(path, response.getOutputStream());
		} catch (final IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR,
				"IOException occurred when streaming file for publication with id '%s': %s".formatted(id, e.getMessage()));
		}
	}

	private Map<Integer, String> loadPubliktypLookup() {
		return publTypRepository.findAll().stream()
			.filter(entity -> entity.getId() != null)
			.collect(Collectors.toMap(PublTypEntity::getId, PublTypEntity::getPubliktyp));
	}

	public enum FileVariant {
		LITEN("fil_liten", PublEntity::getFilLiten),
		STOR("fil_stor", PublEntity::getFilStor),
		ORIGINAL("fil_original", PublEntity::getFilOriginal),
		TXT("fil_txt", PublEntity::getFilTxt),
		XTRA("fil_xtra", PublEntity::getFilXtra);

		private final String subfolder;
		private final Function<PublEntity, String> fileNameExtractor;

		FileVariant(final String subfolder, final Function<PublEntity, String> fileNameExtractor) {
			this.subfolder = subfolder;
			this.fileNameExtractor = fileNameExtractor;
		}

		String extract(final PublEntity entity) {
			return fileNameExtractor.apply(entity);
		}

		String getSubfolder() {
			return subfolder;
		}
	}
}
