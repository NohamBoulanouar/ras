package altn72.ras.controleur;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

final class UtilisateursDeTest {

    private UtilisateursDeTest() {
    }

    static RequestPostProcessor agent() {
        return user("t.durand").roles("AGENT");
    }

    static RequestPostProcessor responsable() {
        return user("c.martin").roles("RESPONSABLE");
    }
}
