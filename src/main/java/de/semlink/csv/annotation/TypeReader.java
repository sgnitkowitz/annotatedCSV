package de.semlink.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.semlink.csv.ITypeReader;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeReader {

	Class<? extends ITypeReader> typeReader();
	
}
