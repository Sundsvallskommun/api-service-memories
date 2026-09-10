package se.sundsvall.memories.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.memories.api.model.Topography;
import se.sundsvall.memories.integration.db.TopographyRepository;
import se.sundsvall.memories.service.mapper.TopographyMapper;

@Service
public class TopographyService {

	private final TopographyRepository topographyRepository;

	public TopographyService(final TopographyRepository topographyRepository) {
		this.topographyRepository = topographyRepository;
	}

	@Transactional(readOnly = true)
	public List<Topography> getTopographies() {
		return TopographyMapper.toTopographyList(topographyRepository.findAllSelectable());
	}
}
