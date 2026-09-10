package se.sundsvall.memories.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.api.model.Topography;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.service.mapper.TopographyMapper;

import static java.util.Comparator.comparing;
import static se.sundsvall.memories.service.util.Names.swedishOrder;

@Service
public class TopographyService {

	private final TopographyRepository topographyRepository;

	public TopographyService(final TopographyRepository topographyRepository) {
		this.topographyRepository = topographyRepository;
	}

	/**
	 * The places a search form can offer. The repository leaves out the rows blank in every column, but in the
	 * database's terms; {@link Topography#getDisplayName()} is what the response actually shows, so it has the last word
	 * on whether a row has a name to show. Ordered here rather than in SQL, see
	 * {@link se.sundsvall.memories.service.util.Names}.
	 */
	@Transactional(readOnly = true)
	public List<Topography> getTopographies() {
		return TopographyMapper.toTopographyList(topographyRepository.findAllSelectable()).stream()
			.filter(topography -> topography.getDisplayName() != null)
			.sorted(comparing(Topography::getDisplayName, swedishOrder())
				.thenComparing(Topography::getTopographyId))
			.toList();
	}
}
