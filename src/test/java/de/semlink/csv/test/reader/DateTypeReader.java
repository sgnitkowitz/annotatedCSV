package de.semlink.csv.test.reader;

import java.util.Date;

import de.semlink.csv.ITypeReader;

public class DateTypeReader implements ITypeReader {

	@Override
	public Object read(String input) {
		return new Date(Long.parseLong(input));
	}

}
