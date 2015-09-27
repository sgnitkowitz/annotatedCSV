package de.semlink.csv.test.writer;

import java.util.Date;

import de.semlink.csv.ITypeWriter;

public class DateTypeWriter implements ITypeWriter {

	@Override
	public String write(Object input) {
		return String.valueOf(((Date) input).getTime());
	}


}
