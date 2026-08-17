package se.sundsvall.memories.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Census record (mantal) model")
public class CensusRecord {

	@Schema(description = "Census record ID", examples = "123")
	private Integer id;

	@Schema(description = "Object number", examples = "SE/1234")
	private String objectNumber;

	@Schema(description = "Source", examples = "MTL")
	private String source;

	@Schema(description = "Property number 1", examples = "Norrmalm 3")
	private String propertyNumber1;

	@Schema(description = "Property part 1", examples = "1/2")
	private String propertyPart1;

	@Schema(description = "Property number 2", examples = "Söder 5")
	private String propertyNumber2;

	@Schema(description = "Property part 2", examples = "1/4")
	private String propertyPart2;

	@Schema(description = "Property number 3", examples = "Väster 7")
	private String propertyNumber3;

	@Schema(description = "Property part 3", examples = "1/8")
	private String propertyPart3;

	@Schema(description = "Serial number (löpnummer)", examples = "12")
	private String serialNumber;

	@Schema(description = "Household number", examples = "3")
	private String householdNumber;

	@Schema(description = "Order number", examples = "1")
	private String orderNumber;

	@Schema(description = "Farm number", examples = "7")
	private String farmNumber;

	@Schema(description = "Occupation / relation", examples = "Bonde")
	private String occupationRelation;

	@Schema(description = "Relation code", examples = "H")
	private String relationCode;

	@Schema(description = "First name", examples = "Anton")
	private String firstName;

	@Schema(description = "Last name", examples = "Nordin")
	private String lastName;

	@Schema(description = "Gender", examples = "man")
	private String gender;

	@Schema(description = "Birth year (stored as free text)", examples = "1852")
	private String birthYear;

	@Schema(description = "Note (anmärkning)", examples = "Flyttade in 1875")
	private String note;

	public static CensusRecord create() {
		return new CensusRecord();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public CensusRecord withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getObjectNumber() {
		return objectNumber;
	}

	public void setObjectNumber(final String objectNumber) {
		this.objectNumber = objectNumber;
	}

	public CensusRecord withObjectNumber(final String objectNumber) {
		this.objectNumber = objectNumber;
		return this;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	public CensusRecord withSource(final String source) {
		this.source = source;
		return this;
	}

	public String getPropertyNumber1() {
		return propertyNumber1;
	}

	public void setPropertyNumber1(final String propertyNumber1) {
		this.propertyNumber1 = propertyNumber1;
	}

	public CensusRecord withPropertyNumber1(final String propertyNumber1) {
		this.propertyNumber1 = propertyNumber1;
		return this;
	}

	public String getPropertyPart1() {
		return propertyPart1;
	}

	public void setPropertyPart1(final String propertyPart1) {
		this.propertyPart1 = propertyPart1;
	}

	public CensusRecord withPropertyPart1(final String propertyPart1) {
		this.propertyPart1 = propertyPart1;
		return this;
	}

	public String getPropertyNumber2() {
		return propertyNumber2;
	}

	public void setPropertyNumber2(final String propertyNumber2) {
		this.propertyNumber2 = propertyNumber2;
	}

	public CensusRecord withPropertyNumber2(final String propertyNumber2) {
		this.propertyNumber2 = propertyNumber2;
		return this;
	}

	public String getPropertyPart2() {
		return propertyPart2;
	}

	public void setPropertyPart2(final String propertyPart2) {
		this.propertyPart2 = propertyPart2;
	}

	public CensusRecord withPropertyPart2(final String propertyPart2) {
		this.propertyPart2 = propertyPart2;
		return this;
	}

	public String getPropertyNumber3() {
		return propertyNumber3;
	}

	public void setPropertyNumber3(final String propertyNumber3) {
		this.propertyNumber3 = propertyNumber3;
	}

	public CensusRecord withPropertyNumber3(final String propertyNumber3) {
		this.propertyNumber3 = propertyNumber3;
		return this;
	}

	public String getPropertyPart3() {
		return propertyPart3;
	}

	public void setPropertyPart3(final String propertyPart3) {
		this.propertyPart3 = propertyPart3;
	}

	public CensusRecord withPropertyPart3(final String propertyPart3) {
		this.propertyPart3 = propertyPart3;
		return this;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(final String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public CensusRecord withSerialNumber(final String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public String getHouseholdNumber() {
		return householdNumber;
	}

	public void setHouseholdNumber(final String householdNumber) {
		this.householdNumber = householdNumber;
	}

	public CensusRecord withHouseholdNumber(final String householdNumber) {
		this.householdNumber = householdNumber;
		return this;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public CensusRecord withOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
		return this;
	}

	public String getFarmNumber() {
		return farmNumber;
	}

	public void setFarmNumber(final String farmNumber) {
		this.farmNumber = farmNumber;
	}

	public CensusRecord withFarmNumber(final String farmNumber) {
		this.farmNumber = farmNumber;
		return this;
	}

	public String getOccupationRelation() {
		return occupationRelation;
	}

	public void setOccupationRelation(final String occupationRelation) {
		this.occupationRelation = occupationRelation;
	}

	public CensusRecord withOccupationRelation(final String occupationRelation) {
		this.occupationRelation = occupationRelation;
		return this;
	}

	public String getRelationCode() {
		return relationCode;
	}

	public void setRelationCode(final String relationCode) {
		this.relationCode = relationCode;
	}

	public CensusRecord withRelationCode(final String relationCode) {
		this.relationCode = relationCode;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public CensusRecord withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public CensusRecord withLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public CensusRecord withGender(final String gender) {
		this.gender = gender;
		return this;
	}

	public String getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(final String birthYear) {
		this.birthYear = birthYear;
	}

	public CensusRecord withBirthYear(final String birthYear) {
		this.birthYear = birthYear;
		return this;
	}

	public String getNote() {
		return note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public CensusRecord withNote(final String note) {
		this.note = note;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final CensusRecord that = (CensusRecord) o;
		return Objects.equals(id, that.id) && Objects.equals(objectNumber, that.objectNumber) && Objects.equals(source, that.source)
			&& Objects.equals(propertyNumber1, that.propertyNumber1) && Objects.equals(propertyPart1, that.propertyPart1) && Objects.equals(propertyNumber2, that.propertyNumber2)
			&& Objects.equals(propertyPart2, that.propertyPart2) && Objects.equals(propertyNumber3, that.propertyNumber3) && Objects.equals(propertyPart3, that.propertyPart3)
			&& Objects.equals(serialNumber, that.serialNumber) && Objects.equals(householdNumber, that.householdNumber) && Objects.equals(orderNumber, that.orderNumber)
			&& Objects.equals(farmNumber, that.farmNumber) && Objects.equals(occupationRelation, that.occupationRelation) && Objects.equals(relationCode, that.relationCode)
			&& Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(gender, that.gender) && Objects.equals(birthYear, that.birthYear)
			&& Objects.equals(note, that.note);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, objectNumber, source, propertyNumber1, propertyPart1, propertyNumber2, propertyPart2, propertyNumber3, propertyPart3, serialNumber, householdNumber, orderNumber,
			farmNumber, occupationRelation, relationCode, firstName, lastName, gender, birthYear, note);
	}

	@Override
	public String toString() {
		return "CensusRecord{" +
			"id=" + id +
			", objectNumber='" + objectNumber + '\'' +
			", source='" + source + '\'' +
			", propertyNumber1='" + propertyNumber1 + '\'' +
			", propertyPart1='" + propertyPart1 + '\'' +
			", propertyNumber2='" + propertyNumber2 + '\'' +
			", propertyPart2='" + propertyPart2 + '\'' +
			", propertyNumber3='" + propertyNumber3 + '\'' +
			", propertyPart3='" + propertyPart3 + '\'' +
			", serialNumber='" + serialNumber + '\'' +
			", householdNumber='" + householdNumber + '\'' +
			", orderNumber='" + orderNumber + '\'' +
			", farmNumber='" + farmNumber + '\'' +
			", occupationRelation='" + occupationRelation + '\'' +
			", relationCode='" + relationCode + '\'' +
			", firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			", gender='" + gender + '\'' +
			", birthYear='" + birthYear + '\'' +
			", note='" + note + '\'' +
			'}';
	}
}
