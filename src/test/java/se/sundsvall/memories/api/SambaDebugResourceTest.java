package se.sundsvall.memories.api;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.memories.integration.samba.SambaIntegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class SambaDebugResourceTest {

	@Mock
	private SambaIntegration sambaIntegrationMock;

	@InjectMocks
	private SambaDebugResource resource;

	@Test
	void listDelegatesToIntegration() {
		final var path = "/MINNEN/MEDIA/TEXT/fil_stor/";
		when(sambaIntegrationMock.list(path)).thenReturn(List.of("a.jpg", "b.jpg"));

		final var response = resource.list(path);

		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat(response.getBody()).containsExactly("a.jpg", "b.jpg");
		verify(sambaIntegrationMock).list(path);
	}

	@Test
	void listRejectsPathTraversal() {
		final var exception = assertThrows(ThrowableProblem.class, () -> resource.list("/MINNEN/../etc"));

		assertThat(exception.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(exception.getMessage()).contains("must not contain '..'");
		verifyNoInteractions(sambaIntegrationMock);
	}
}
