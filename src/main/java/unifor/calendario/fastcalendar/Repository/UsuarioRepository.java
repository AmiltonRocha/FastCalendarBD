package unifor.calendario.fastcalendar.Repository;
//ele importa os comandos necessários para criar o repositório de usuarios comandos CRUD
//CREATE, READ, UPDATE, DELETE

import unifor.calendario.fastcalendar.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
     //ELE ESTA CRIANDO UM FINDBY PERSONALIZADO (OBS ELE JA CRIA O ID E SALVAR POR CONTA DO JPA)
    Optional<Usuario> findByMatricula(String matricula);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNome(String nome);
       
} 
