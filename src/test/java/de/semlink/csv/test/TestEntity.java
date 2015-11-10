package de.semlink.csv.test;

import java.util.Date;

import de.semlink.csv.annotation.CSVEntity;
import de.semlink.csv.annotation.CSVItem;
import de.semlink.csv.annotation.TypeReader;
import de.semlink.csv.annotation.TypeWriter;
import de.semlink.csv.test.reader.DateTypeReader;
import de.semlink.csv.test.writer.DateTypeWriter;

@CSVEntity
public class TestEntity extends RootEntity {

	@CSVItem
	private Long id;
	@CSVItem
	private String firstName;
	@CSVItem
	private String lastName;
	@CSVItem
	@TypeWriter(typeWriter = DateTypeWriter.class)
	@TypeReader(typeReader = DateTypeReader.class)
	private Date date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "TestEntity [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", date=" + date + "]";
	}

}
