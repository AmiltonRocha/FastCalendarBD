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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario") 
public class Usuario {
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
    
    
    
}
