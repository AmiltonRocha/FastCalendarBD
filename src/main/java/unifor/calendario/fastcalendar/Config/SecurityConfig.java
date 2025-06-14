package unifor.calendario.fastcalendar.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import unifor.calendario.fastcalendar.Model.Usuario;
import java.util.Arrays;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults()) // Habilita a configuração de CORS
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs REST
            .authorizeHttpRequests(authz -> authz
                // Rotas públicas
                .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Rotas de Admin
                .requestMatchers("/usuarios/*/promoverAdmin", "/usuarios/*/rebaixarUser").hasAuthority(Usuario.Cargo.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasAuthority(Usuario.Cargo.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/usuarios").hasAuthority(Usuario.Cargo.ADMIN.name())

                // Rotas Autenticadas (USER ou ADMIN)
                .requestMatchers(HttpMethod.GET, "/usuarios/me").authenticated() // Endpoint de login
                .requestMatchers(HttpMethod.GET, "/usuarios/{id}").authenticated()
                .requestMatchers(HttpMethod.PUT, "/usuarios/{id}").authenticated() // Rota de PUT ajustada
                .requestMatchers("/eventos/**").authenticated()

                // Qualquer outra requisição precisa de autenticação
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults()); // Usa HTTP Basic Authentication

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}