package de.semlink.csv;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.List;

import de.semlink.csv.annotation.CSVEntity;
import de.semlink.csv.annotation.CSVItem;
import de.semlink.csv.annotation.TypeWriter;
import de.semlink.csv.exception.NoCSVEntityException;

public class CSVBuilder {

	private PrintWriter writer;
	private CSVEntity csvEntity;
	private int columns;

	private CSVBuilder() {
	}

	public static CSVBuilder getInstance() {
		return new CSVBuilder();
	}

	public void buildCSV(List<?> csvEntities, OutputStream out) {
		try {
			if (csvEntities.size() == 0) {
				return;
			}

			csvEntity = getCsvEntity(csvEntities.get(0));
			columns = getTotalColumn(csvEntities.get(0));
			writer = new PrintWriter(out);

			if (csvEntity.writeHeader()) {
				writeLine(getHeader(csvEntities.get(0)));
			}

			writeLines(csvEntities);
		} finally {
			cleanBuilder();
		}
	}

	private CSVEntity getCsvEntity(Object o) {
		if (o.getClass().isAnnotationPresent(CSVEntity.class)) {
			return o.getClass().getAnnotation(CSVEntity.class);
		}

		throw new NoCSVEntityException();
	}

	private int getTotalColumn(Object o) {
		int columns = 0;

		// TODO optimize
		for (Field field : o.getClass().getSuperclass().getDeclaredFields()) {
			if (field.isAnnotationPresent(CSVItem.class)) {
				columns++;
			}
		}

		for (Field field : o.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(CSVItem.class)) {
				columns++;
			}
		}

		return columns;
	}

	private String[] getHeader(Object o) {
		String[] header = new String[columns];
		int currentColumn = 0;

		// TODO optimize
		for (Field field : o.getClass().getSuperclass().getDeclaredFields()) {
			if (field.isAnnotationPresent(CSVItem.class)) {
				CSVItem csvItem = field.getAnnotation(CSVItem.class);

				String rowName = csvItem.columnName();
				int rowNumber = csvItem.columnNumber();

				if (rowNumber >= 0) {
					if (rowName.isEmpty()) {
						header[rowNumber] = field.getName();
					} else {
						header[rowNumber] = rowName;
					}
				} else {
					if (rowName.isEmpty()) {
						header[currentColumn++] = field.getName();
					} else {
						header[currentColumn++] = rowName;
					}
				}
			}
		}

		for (Field field : o.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(CSVItem.class)) {
				CSVItem csvItem = field.getAnnotation(CSVItem.class);

				String rowName = csvItem.columnName();
				int rowNumber = csvItem.columnNumber();

				if (rowNumber >= 0) {
					if (rowName.isEmpty()) {
						header[rowNumber] = field.getName();
					} else {
						header[rowNumber] = rowName;
					}
				} else {
					if (rowName.isEmpty()) {
						header[currentColumn++] = field.getName();
					} else {
						header[currentColumn++] = rowName;
					}
				}
			}
		}

		return header;
	}

	private String[] getLine(Object o) {
		String[] line = new String[columns];
		int currentColumn = 0;

		// TODO optimize
		for (Field field : o.getClass().getSuperclass().getDeclaredFields()) {
			if (field.isAnnotationPresent(CSVItem.class)) {
				try {
					field.setAccessible(true);
					Object value;
					if (field.isAnnotationPresent(TypeWriter.class)) {
						Class<? extends ITypeWriter> typeReaderClass = field.getAnnotation(TypeWriter.class).typeWriter();
						value = typeReaderClass.newInstance().write(field.get(o));
					} else {
						value = field.get(o);
					}
					CSVItem csvItem = field.getAnnotation(CSVItem.class);
					int rowNumber = csvItem.columnNumber();

					if (rowNumber >= 0) {
						line[rowNumber] = String.valueOf(value);
					} else {
						line[currentColumn++] = String.valueOf(value);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (Field field : o.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(CSVItem.class)) {
				try {
					field.setAccessible(true);
					Object value;
					if (field.isAnnotationPresent(TypeWriter.class)) {
						Class<? extends ITypeWriter> typeReaderClass = field.getAnnotation(TypeWriter.class).typeWriter();
						value = typeReaderClass.newInstance().write(field.get(o));
					} else {
						value = field.get(o);
					}
					CSVItem csvItem = field.getAnnotation(CSVItem.class);
					int rowNumber = csvItem.columnNumber();

					if (rowNumber >= 0) {
						line[rowNumber] = String.valueOf(value);
					} else {
						line[currentColumn++] = String.valueOf(value);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return line;
	}

	private void writeLines(List<?> entities) {
		for (Object o : entities) {
			String[] line = getLine(o);
			writeLine(line);
		}

		writer.flush();
	}

	private void writeLine(String[] line) {
		for (int i = 0; i < line.length; i++) {
			if (i != 0) {
				writer.print(csvEntity.delimiter());
			}

			writer.print(line[i]);
		}

		writer.println();
	}

	private void cleanBuilder() {
		writer = null;
		csvEntity = null;
	}
}
