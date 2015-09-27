package de.semlink.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.semlink.csv.ITypeWriter;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeWriter {

	Class<? extends ITypeWriter> typeWriter();
	
}
