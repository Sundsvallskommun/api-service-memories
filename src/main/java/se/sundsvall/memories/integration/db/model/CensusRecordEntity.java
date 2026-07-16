package se.sundsvall.memories.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * Entity for the {@code MANTAL} census-record table. The table has no {@code OPTIONS} (publish) column and no
 * parish columns, so census records carry no publish filter.
 */
@Entity
@Table(name = "MANTAL")
public class CensusRecordEntity {

	@Id
	@Column(name = "ID")
	private Integer id;

	@Column(name = "OBJEKTSNR", length = 36)
	private String objectNumber;

	@Column(name = "KALLA", length = 10)
	private String source;

	@Column(name = "MFSTNR1", length = 100)
	private String propertyNumber1;

	@Column(name = "FSTDEL1", length = 100)
	private String propertyPart1;

	@Column(name = "MFSTNR2", length = 100)
	private String propertyNumber2;

	@Column(name = "FSTDEL2", length = 100)
	private String propertyPart2;

	@Column(name = "MFSTNR3", length = 100)
	private String propertyNumber3;

	@Column(name = "FSTDEL3", length = 100)
	private String propertyPart3;

	@Column(name = "LOPNR", length = 10)
	private String serialNumber;

	@Column(name = "HUSHNR", length = 10)
	private String householdNumber;

	@Column(name = "ORDNR", length = 10)
	private String orderNumber;

	@Column(name = "FNR", length = 10)
	private String farmNumber;

	@Column(name = "YRKREL", length = 100)
	private String occupationRelation;

	@Column(name = "RELKOD", length = 10)
	private String relationCode;

	@Column(name = "MNMNF", length = 100)
	private String firstName;

	@Column(name = "MNMNE", length = 100)
	private String lastName;

	@Column(name = "KON", length = 10)
	private String gender;

	@Column(name = "FODAR", length = 100)
	private String birthYear;

	@Column(name = "ANM", length = 2000)
	private String note;

	public static CensusRecordEntity create() {
		return new CensusRecordEntity();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public CensusRecordEntity withId(final Integer id) {
		this.id = id;
		return this;
	}

	public String getObjectNumber() {
		return objectNumber;
	}

	public void setObjectNumber(final String objectNumber) {
		this.objectNumber = objectNumber;
	}

	public CensusRecordEntity withObjectNumber(final String objectNumber) {
		this.objectNumber = objectNumber;
		return this;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	public CensusRecordEntity withSource(final String source) {
		this.source = source;
		return this;
	}

	public String getPropertyNumber1() {
		return propertyNumber1;
	}

	public void setPropertyNumber1(final String propertyNumber1) {
		this.propertyNumber1 = propertyNumber1;
	}

	public CensusRecordEntity withPropertyNumber1(final String propertyNumber1) {
		this.propertyNumber1 = propertyNumber1;
		return this;
	}

	public String getPropertyPart1() {
		return propertyPart1;
	}

	public void setPropertyPart1(final String propertyPart1) {
		this.propertyPart1 = propertyPart1;
	}

	public CensusRecordEntity withPropertyPart1(final String propertyPart1) {
		this.propertyPart1 = propertyPart1;
		return this;
	}

	public String getPropertyNumber2() {
		return propertyNumber2;
	}

	public void setPropertyNumber2(final String propertyNumber2) {
		this.propertyNumber2 = propertyNumber2;
	}

	public CensusRecordEntity withPropertyNumber2(final String propertyNumber2) {
		this.propertyNumber2 = propertyNumber2;
		return this;
	}

	public String getPropertyPart2() {
		return propertyPart2;
	}

	public void setPropertyPart2(final String propertyPart2) {
		this.propertyPart2 = propertyPart2;
	}

	public CensusRecordEntity withPropertyPart2(final String propertyPart2) {
		this.propertyPart2 = propertyPart2;
		return this;
	}

	public String getPropertyNumber3() {
		return propertyNumber3;
	}

	public void setPropertyNumber3(final String propertyNumber3) {
		this.propertyNumber3 = propertyNumber3;
	}

	public CensusRecordEntity withPropertyNumber3(final String propertyNumber3) {
		this.propertyNumber3 = propertyNumber3;
		return this;
	}

	public String getPropertyPart3() {
		return propertyPart3;
	}

	public void setPropertyPart3(final String propertyPart3) {
		this.propertyPart3 = propertyPart3;
	}

	public CensusRecordEntity withPropertyPart3(final String propertyPart3) {
		this.propertyPart3 = propertyPart3;
		return this;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(final String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public CensusRecordEntity withSerialNumber(final String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public String getHouseholdNumber() {
		return householdNumber;
	}

	public void setHouseholdNumber(final String householdNumber) {
		this.householdNumber = householdNumber;
	}

	public CensusRecordEntity withHouseholdNumber(final String householdNumber) {
		this.householdNumber = householdNumber;
		return this;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public CensusRecordEntity withOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
		return this;
	}

	public String getFarmNumber() {
		return farmNumber;
	}

	public void setFarmNumber(final String farmNumber) {
		this.farmNumber = farmNumber;
	}

	public CensusRecordEntity withFarmNumber(final String farmNumber) {
		this.farmNumber = farmNumber;
		return this;
	}

	public String getOccupationRelation() {
		return occupationRelation;
	}

	public void setOccupationRelation(final String occupationRelation) {
		this.occupationRelation = occupationRelation;
	}

	public CensusRecordEntity withOccupationRelation(final String occupationRelation) {
		this.occupationRelation = occupationRelation;
		return this;
	}

	public String getRelationCode() {
		return relationCode;
	}

	public void setRelationCode(final String relationCode) {
		this.relationCode = relationCode;
	}

	public CensusRecordEntity withRelationCode(final String relationCode) {
		this.relationCode = relationCode;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public CensusRecordEntity withFirstName(final String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public CensusRecordEntity withLastName(final String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public CensusRecordEntity withGender(final String gender) {
		this.gender = gender;
		return this;
	}

	public String getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(final String birthYear) {
		this.birthYear = birthYear;
	}

	public CensusRecordEntity withBirthYear(final String birthYear) {
		this.birthYear = birthYear;
		return this;
	}

	public String getNote() {
		return note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public CensusRecordEntity withNote(final String note) {
		this.note = note;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final CensusRecordEntity that = (CensusRecordEntity) o;
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
		return "CensusRecordEntity{" +
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
