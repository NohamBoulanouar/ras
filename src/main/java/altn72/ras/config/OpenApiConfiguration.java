package altn72.ras.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    private static final String AUTHENTIFICATION_BASIQUE = "basique";

    @Bean
    public OpenAPI documentationApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("RAS — Rien À Signaler : API des incidents")
                        .description("Consultation des incidents et modification de leur état. "
                                + "Authentification HTTP Basic avec les comptes de l'application.")
                        .version("1.0"))
                .components(new Components().addSecuritySchemes(AUTHENTIFICATION_BASIQUE,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(AUTHENTIFICATION_BASIQUE));
    }
}
