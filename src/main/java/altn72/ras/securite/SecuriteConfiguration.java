package altn72.ras.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecuriteConfiguration {

    @Bean
    public SecurityFilterChain filtreSecurite(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(acces -> acces
                        .requestMatchers("/connexion", "/css/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formulaire -> formulaire
                        .loginPage("/connexion")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(deconnexion -> deconnexion
                        .logoutSuccessUrl("/connexion?deconnexion")
                        .permitAll());
        return http.build();
    }
}
