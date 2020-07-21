package valoeghese.epic.abstraction;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Priority {
	/**
	 * @return the priority of the method. The default for non-annotated methods should be 0. Higher numbers are run first.
	 */
	int value();
}
