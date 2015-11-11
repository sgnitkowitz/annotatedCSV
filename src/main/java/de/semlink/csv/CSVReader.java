package de.semlink.csv;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import de.semlink.csv.annotation.CSVEntity;
import de.semlink.csv.annotation.CSVItem;
import de.semlink.csv.annotation.TypeReader;
import de.semlink.csv.exception.NoCSVEntityException;

public class CSVReader<T> {

	private Class<T> c;
	private Scanner scanner;
	private CSVEntity csvEntity;
	private Map<Integer, String> superColumnToFieldMap;
	private Map<Integer, String> columnToFieldMap;

	private CSVReader(Class<T> c) {
		this.c = c;
	}

	public static <T> CSVReader<T> getInstance(Class<T> c) {
		return new CSVReader<>(c);
	}

	public List<T> readCSV(InputStream in) {
		try {
			csvEntity = getCsvEntity();
			superColumnToFieldMap = getSuperColumnToFieldMap();
			columnToFieldMap = getColumnToFieldMap();
			scanner = new Scanner(in);

			if (csvEntity.writeHeader() && scanner.hasNextLine()) {
				scanner.nextLine();
			}

			return readEntities();
		} finally {
			cleanReader();
		}
	}

	private CSVEntity getCsvEntity() {
		if (c.isAnnotationPresent(CSVEntity.class)) {
			return c.getAnnotation(CSVEntity.class);
		}

		throw new NoCSVEntityException();
	}

	private Map<Integer, String> getColumnToFieldMap() {
		Map<Integer, String> map = new HashMap<>();
		int currentColumn = 0 + superColumnToFieldMap.size();

		for (Field field : c.getDeclaredFields()) {
			if (field.isAnnotationPresent(CSVItem.class)) {
				CSVItem csvItem = field.getAnnotation(CSVItem.class);
				int rowNumber = csvItem.columnNumber();

				if (rowNumber >= 0) {
					map.put(rowNumber, field.getName());
				} else {
					map.put(currentColumn++, field.getName());
				}
			}
		}

		return map;
	}

	private Map<Integer, String> getSuperColumnToFieldMap() {
		// TODO optimize
		Map<Integer, String> map = new HashMap<>();
		int currentColumn = 0;
		
		for (Field field : c.getSuperclass().getDeclaredFields()) {
			if (field.isAnnotationPresent(CSVItem.class)) {
				CSVItem csvItem = field.getAnnotation(CSVItem.class);
				int rowNumber = csvItem.columnNumber();
				
				if (rowNumber >= 0) {
					map.put(rowNumber, field.getName());
				} else {
					map.put(currentColumn++, field.getName());
				}
			}
		}
		
		return map;
	}

	private List<T> readEntities() {
		List<T> entities = new ArrayList<>();
		T entity;

		while ((entity = readEntity()) != null) {
			entities.add(entity);
		}

		return entities;
	}

	private T readEntity() {
		if (scanner.hasNextLine()) {
			try {
				T entity = c.newInstance();
				String[] cells = scanner.nextLine().split(getDelimiter());
				updateEntity(entity, cells);

				return entity;
			} catch (Exception e) {
				return null;
			}
		}

		return null;
	}

	public String getDelimiter() {
		String delimiter = csvEntity.delimiter();
		
		if (delimiter.equals(".")) {
			delimiter = "\\.";
		} else if (delimiter.equals("\\")) {
			delimiter = "\\\\";
		}
		
		return delimiter;
	}

	public void updateEntity(T entity, String[] cells) {
		for (int i = 0; i < cells.length; i++) {
			try {
				String cell = cells[i];
				
				String fieldName = superColumnToFieldMap.get(i);
				Field field;
				if (fieldName == null) {
					fieldName = columnToFieldMap.get(i);
					field = entity.getClass().getDeclaredField(fieldName);
				} else {
					field = entity.getClass().getSuperclass().getDeclaredField(fieldName);
				}
				
				field.setAccessible(true);

				if (field.isAnnotationPresent(TypeReader.class)) {
					Class<? extends ITypeReader> typeReaderClass = field.getAnnotation(TypeReader.class).typeReader();
					field.set(entity, typeReaderClass.newInstance().read(cell));
				} else {
					field.set(entity, cellToSimpleObject(field.getType(), cell));
				}

			} catch (Exception e) {
				// TODO logging
			}
		}
	}

	private Object cellToSimpleObject(Class<?> c, String cell) {
		Object o;

		if (c.equals(Long.class) || c.equals(long.class)) {
			o = Long.parseLong(cell);
		} else if (c.equals(Integer.class) || c.equals(int.class)) {
			o = Integer.parseInt(cell);
		} else if (c.equals(Double.class) || c.equals(double.class)) {
			o = Double.parseDouble(cell);
		} else if (c.equals(Float.class) || c.equals(float.class)) {
			o = Float.parseFloat(cell);
		} else if (c.equals(Boolean.class) || c.equals(boolean.class)) {
			o = Boolean.parseBoolean(cell);
		} else if (c.equals(BigInteger.class)) {
			o = new BigInteger(cell);
		} else {
			o = cell;
		}

		return o;
	}

	private void cleanReader() {
		scanner = null;
		csvEntity = null;
		columnToFieldMap = null;
	}
}
