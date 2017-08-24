import com.google.inject.AbstractModule;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 * <p>
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        //Alle Interfaces von den Implementationen in controllers.implementations durch diese injecten lassen
        for (Class<?> aClass : new Reflections("controllers.implementations", new SubTypesScanner(false)).getSubTypesOf(Object.class)) {
            bindImplementedInterfaces(aClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void bindImplementedInterfaces(Class<?> aClass) {
        for (Class<?> interfaces : aClass.getInterfaces()) {
            bind((Class<? super Object>) interfaces).to(aClass);
        }
    }

}
