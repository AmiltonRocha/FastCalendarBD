package unifor.calendario.fastcalendar.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import unifor.calendario.fastcalendar.Model.Usuario;
import unifor.calendario.fastcalendar.Repository.UsuarioRepository; // Certifique-se que o import está correto

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Assumindo que 'username' é o email para login, conforme definido em Usuario.java
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + username));
        // Como Usuario implementa UserDetails, podemos retorná-lo diretamente.
        return usuario;
    }
}