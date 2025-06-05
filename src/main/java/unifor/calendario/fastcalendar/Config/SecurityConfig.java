package unifor.calendario.fastcalendar.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import unifor.calendario.fastcalendar.Model.Usuario; // Importe seu enum Cargo

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
            .csrf(csrf -> csrf.disable()) // Desabilitar CSRF para simplificar testes com Postman. Habilite em produção para aplicações web tradicionais.
            .authorizeHttpRequests(authz -> authz
                // Permitir acesso público para criar usuários (registro)
                .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                // Permitir acesso público para a documentação da API (se você usar Swagger/OpenAPI)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Proteger endpoints de gerenciamento de usuários (promover/rebaixar) - apenas ADMIN
                .requestMatchers("/usuarios/*/promoverAdmin", "/usuarios/*/rebaixarUser").hasAuthority(Usuario.Cargo.ADMIN.name())
                // Proteger outros endpoints de usuários - apenas ADMIN para DELETE, PUT e GET de todos
                .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasAuthority(Usuario.Cargo.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/usuarios/**").hasAuthority(Usuario.Cargo.ADMIN.name()) // Permitir que o próprio usuário atualize seus dados pode ser uma regra mais granular
                .requestMatchers(HttpMethod.GET, "/usuarios").hasAuthority(Usuario.Cargo.ADMIN.name())
                 // Permitir que usuários autenticados acessem seu próprio perfil
                .requestMatchers(HttpMethod.GET, "/usuarios/{id}").authenticated() // Poderia ser mais específico para permitir apenas o próprio usuário ou admin
                // Proteger endpoints de eventos - qualquer usuário autenticado pode gerenciar seus eventos
                .requestMatchers("/eventos/**").authenticated()
                // Qualquer outra requisição precisa estar autenticada
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults()) // Usar HTTP Basic Authentication (bom para APIs e testes iniciais)
            .formLogin(withDefaults()); // Ou .formLogin(withDefaults()) se preferir um formulário de login gerado pelo Spring Security

        return http.build();
    }
}