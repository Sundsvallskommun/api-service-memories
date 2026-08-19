package se.sundsvall.memories.service.model;

public enum FileVariant {

	THUMBNAIL("fil_liten"),
	LARGE("fil_stor"),
	TEXT("fil_txt"),
	ORIGINAL("fil_original");

	private final String subfolder;

	FileVariant(final String subfolder) {
		this.subfolder = subfolder;
	}

	public String getSubfolder() {
		return subfolder;
	}
}
