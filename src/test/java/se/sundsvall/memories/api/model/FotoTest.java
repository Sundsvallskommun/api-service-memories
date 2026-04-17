package se.sundsvall.memories.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class FotoTest {

	@Test
	void testBean() {
		assertThat(Foto.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = Foto.create()
			.withFotoId(1234)
			.withDoktitel("Stadsvy")
			.withTidig("1920")
			.withSenast("1925")
			.withFotoOplats("Sundsvall")
			.withFilLiten("FOTO.id_1234_fil_liten.jpg")
			.withFilStor("FOTO.id_1234_fil_stor.jpg")
			.withFilOriginal("FOTO.id_1234_fil_original.jpg")
			.withGivRattigh("Free")
			.withGivForbeh("Nej");

		assertThat(result.getFotoId()).isEqualTo(1234);
		assertThat(result.getDoktitel()).isEqualTo("Stadsvy");
		assertThat(result.getTidig()).isEqualTo("1920");
		assertThat(result.getSenast()).isEqualTo("1925");
		assertThat(result.getFotoOplats()).isEqualTo("Sundsvall");
		assertThat(result.getFilLiten()).isEqualTo("FOTO.id_1234_fil_liten.jpg");
		assertThat(result.getGivRattigh()).isEqualTo("Free");
		assertThat(result.getGivForbeh()).isEqualTo("Nej");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Foto.create()).hasAllNullFieldsOrProperties();
	}
}
