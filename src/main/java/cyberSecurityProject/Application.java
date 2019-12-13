package cyberSecurityProject;

import org.apache.catalina.Context;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .profiles("dev")
                .properties("selenium:false")
                .application()
                .run(args);
    }

}