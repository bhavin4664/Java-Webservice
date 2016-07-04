package drpatients;

import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/resourcesDr")
public class RestfulDrpatient extends Application {
    @Override
    public Set<Class<?>> getClasses() {
	Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(DrpatientsRS.class);
        return set;
    }
}