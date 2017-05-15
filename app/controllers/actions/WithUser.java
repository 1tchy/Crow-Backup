package controllers.actions;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation um eine Methode zu markieren, dass sie nur mit einem Benutzer aufgerufen werden darf.
 * Dies muss vom Aufrufer sichergestellt werden, z.B. für Controller-Aufrufe macht das Play über die @With()-Annotation, wir selbst können das über die @WithExplicitAction()-Annotation tun.
 */
@With(WithUserAction.class)
@WithExplicitAction(WithUserAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithUser {

}
