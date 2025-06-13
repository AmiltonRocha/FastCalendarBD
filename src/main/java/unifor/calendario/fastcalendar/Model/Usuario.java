package unifor.calendario.fastcalendar.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Date;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario") 
public class Usuario implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 80)
    private String email;

    @Column(name = "senha", nullable = false, length = 80)
    private String senha;

    @Column(name = "matricula", nullable = false, length = 80)
    private String matricula;

    @Column(name = "data_nascimento")
    private Date dataNascimento;

        public enum Cargo {
        USER, // Usuário comum
        ADMIN // Administrador
    }

    @Enumerated(EnumType.STRING) // Grava o nome do enum (USER, ADMIN) no banco
    @Column(name = "cargo", nullable = false)
    private Cargo cargo = Cargo.USER;

    public Usuario(String nome, String email, String senha, String matricula, Date dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.matricula = matricula;
        this.dataNascimento = dataNascimento;
        this.cargo = Cargo.USER; // Garante que o padrão seja USER ao usar este construtor
    }
    
        // Implementação dos métodos da interface UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna uma lista de GrantedAuthority baseada no cargo do usuário
        // O nome da autoridade DEVE corresponder ao que você usa em .hasAuthority() ou .hasRole()
        // Se usar .hasRole("ADMIN"), a autoridade deve ser "ROLE_ADMIN"
        // Se usar .hasAuthority("ADMIN"), a autoridade deve ser "ADMIN"
        return List.of(new SimpleGrantedAuthority(this.cargo.name()));
    }

    @Override
    public String getPassword() {
        return this.senha; // Retorna a senha (que será armazenada criptografada)
    }

    @Override
    public String getUsername() {
        return this.email; // Usaremos o email como username para login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Por padrão, a conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Por padrão, a conta não está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Por padrão, as credenciais não expiram
    }

    @Override
    public boolean isEnabled() {
        return true; // Por padrão, o usuário está habilitado
    }



    
}
