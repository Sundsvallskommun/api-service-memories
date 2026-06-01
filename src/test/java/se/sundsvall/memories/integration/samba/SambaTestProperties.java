package se.sundsvall.memories.integration.samba;

/**
 * Shared SMB configuration for tests that construct a {@link SambaIntegration} or a media service directly. The values
 * mirror {@code application-junit.yml} / {@code application-it.yml} so the SMB paths asserted in tests line up. Kept in
 * one place so the (long, positional) {@link SambaIntegrationProperties} constructor only needs maintaining once.
 */
public final class SambaTestProperties {

	private SambaTestProperties() {}

	public static final SambaIntegrationProperties SAMBA_PROPERTIES = new SambaIntegrationProperties(
		"localhost", 445, "WORKGROUP", "user", "password", "/share/", "/film/", "/publ/", "/foto/", "/ljud/", "/text/", "/text_multi/");
}
