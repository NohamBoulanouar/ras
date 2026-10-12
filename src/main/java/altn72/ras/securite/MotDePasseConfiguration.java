package altn72.ras.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MotDePasseConfiguration {
    @Bean
    public PasswordEncoder encodeurMotDePasse() {
        return new BCryptPasswordEncoder();
    }

}
