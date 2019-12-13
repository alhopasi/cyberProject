package cyberSecurityProject.config;

import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CustomCookieFactory implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addContextCustomizers(new TomcatContextCustomizer() {
            @Override
            public void customize(Context cntxt) {
                cntxt.setUseHttpOnly(false);
                overwriteIdGeneratorWithVulnerable(cntxt);
            }
        });
    }

    private void overwriteIdGeneratorWithVulnerable(Context cntxt) {
        System.out.println("HERE WE ARE");
        SessionIdGenerator ses = new StandardSessionIdGenerator() {

            long id = 0;
            boolean generated;

            @Override
            public String generateSessionId() {
                if (!generated) {
                    id = generateId();
                    generated = true;
                }
                id++;
                return String.valueOf(id);
            }

            @Override
            public String generateSessionId(String string) {
                if (!generated) {
                    id = generateId();
                    generated = true;
                }
                id++;
                return String.valueOf(id);
            }
            private long generateId() {
                return new Random().nextLong() / 10;
            }
        };
        Manager manager = cntxt.getManager();
        if (manager == null) {
            manager = new StandardManager();
            cntxt.setManager(manager);
        }

        cntxt.getManager().setSessionIdGenerator(ses);
    }

}

