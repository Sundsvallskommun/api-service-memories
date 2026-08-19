package se.sundsvall.memories.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;
import se.sundsvall.memories.api.model.NodeParameters;
import se.sundsvall.memories.api.model.PagedNodeResponse;
import se.sundsvall.memories.integration.db.NodeRepository;
import se.sundsvall.memories.service.mapper.NodeMapper;

@Service
public class NodeService {

	private final NodeRepository nodeRepository;

	public NodeService(final NodeRepository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	@Transactional(readOnly = true)
	public PagedNodeResponse search(final NodeParameters parameters) {
		final var pageable = PageRequest.of(parameters.getPage() - 1, parameters.getLimit(), parameters.sort());

		final var page = nodeRepository.findAllByParameters(parameters, pageable);

		return PagedNodeResponse.create()
			.withNodes(NodeMapper.toNodeList(page.getContent()))
			.withMetaData(PagingAndSortingMetaData.create().withPageData(page));
	}
}
