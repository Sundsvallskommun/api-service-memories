package se.sundsvall.memories.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.api.model.Institution;
import se.sundsvall.memories.integration.db.InstitutionRepository;
import se.sundsvall.memories.service.mapper.InstitutionMapper;

import static java.util.Comparator.comparing;
import static se.sundsvall.memories.service.util.Names.swedishOrder;

/**
 * The {@code /institutions} dropdown. Read per request rather than cached: the table is a couple of dozen rows.
 */
@Service
public class InstitutionService {

	private final InstitutionRepository institutionRepository;

	public InstitutionService(final InstitutionRepository institutionRepository) {
		this.institutionRepository = institutionRepository;
	}

	/** Ordered here rather than in SQL, see {@link se.sundsvall.memories.service.util.Names}. */
	@Transactional(readOnly = true)
	public List<Institution> getInstitutions() {
		return InstitutionMapper.toInstitutionList(institutionRepository.findAllSelectable()).stream()
			.sorted(comparing(Institution::getName, swedishOrder())
				.thenComparing(Institution::getInstitutionId))
			.toList();
	}
}
