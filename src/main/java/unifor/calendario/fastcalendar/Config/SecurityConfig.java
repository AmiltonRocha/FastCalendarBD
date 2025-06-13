package unifor.calendario.fastcalendar.Config;

import org.springframework.beans.factory.annotation.Autowired;
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
import unifor.calendario.fastcalendar.Service.CustomUserDetailsService; // Importe seu UserDetailsService
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults()) // HABILITA A CONFIGURAÇÃO DE CORS ABAIXO
            .csrf(csrf -> csrf.disable()) // Para APIs ou se estiver usando tokens. Para form login tradicional, considere habilitar.
            .authorizeHttpRequests(authz -> authz
                // ... (o resto das suas regras continua igual)
                .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/usuarios/*/promoverAdmin", "/usuarios/*/rebaixarUser").hasAuthority(Usuario.Cargo.ADMIN.name())
                .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasAuthority(Usuario.Cargo.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/usuarios/**").hasAuthority(Usuario.Cargo.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/usuarios").hasAuthority(Usuario.Cargo.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/usuarios/{id}").authenticated()
                .requestMatchers("/eventos/**").authenticated()
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults())
            .formLogin(withDefaults())
            .userDetailsService(customUserDetailsService) // Define o UserDetailsService para carregar usuários do BD
            .httpBasic(withDefaults()) // Habilita autenticação HTTP Basic
            .formLogin(formLogin -> // Configura o login via formulário
                            formLogin
                           .loginPage("/login") // Define a URL da sua página de login (se tiver uma customizada)
                           .defaultSuccessUrl("/home", true) // Redireciona para /home após login
                            .permitAll() // Permite acesso à página de login para todos
                   )
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll()); // Configura o logout
        return http.build();
    }

    // BEAN DE CONFIGURAÇÃO DO CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite requisições de qualquer origem. Para produção, você pode restringir.
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        // Permite os métodos HTTP mais comuns.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        // Permite todos os cabeçalhos.
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a configuração a todas as rotas.
        return source;
    }
}