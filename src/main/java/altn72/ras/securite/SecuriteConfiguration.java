package altn72.ras.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecuriteConfiguration {

    // L'API est appelée par des programmes, pas par un navigateur : authentification Basic à chaque appel,
    // pas de session ni de jeton CSRF, et un 401 au lieu d'une redirection vers la page de connexion
    @Bean
    @Order(1)
    public SecurityFilterChain filtreSecuriteApi(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(acces -> acces.anyRequest().authenticated())
                .httpBasic(basique -> basique.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filtreSecuriteWeb(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(acces -> acces
                        .requestMatchers("/connexion", "/css/**").permitAll()
                        .requestMatchers("/tableau-de-bord").hasRole("RESPONSABLE")
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
