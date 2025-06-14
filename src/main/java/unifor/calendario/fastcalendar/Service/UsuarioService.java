package unifor.calendario.fastcalendar.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import unifor.calendario.fastcalendar.Model.Usuario;
import unifor.calendario.fastcalendar.Repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    // Adiciona o Logger para diagnóstico
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criarUsuario(Usuario usuario) {
        if (usuarioRepository.findByMatricula(usuario.getMatricula()).isPresent()) {
            throw new IllegalArgumentException("Matrícula já cadastrada: " + usuario.getMatricula());
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado: " + usuario.getEmail());
        }
        
        String senhaPlana = usuario.getSenha();
        String senhaCriptografada = passwordEncoder.encode(senhaPlana);

        // LOG DE DIAGNÓSTICO: Mostra a senha sendo salva
        logger.info("CRIANDO USUÁRIO [{}]: Hash da senha gerado -> [{}]", usuario.getEmail(), senhaCriptografada);

        usuario.setSenha(senhaCriptografada);
        return usuarioRepository.save(usuario);
    }

    // ... (outros métodos como buscarUsuarioPorId, listarTodos, etc. continuam aqui)
    public Optional<Usuario> buscarUsuarioPorId(Long id) { return usuarioRepository.findById(id); }
    public List<Usuario> listarTodosUsuarios() { return usuarioRepository.findAll(); }
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) { /* ... lógica de atualização ... */ return null; }
    public void deletarUsuario(Long id) { usuarioRepository.deleteById(id); }
    public Usuario promoverParaAdmin(Long usuarioId) { /* ... lógica ... */ return null; }
    public Usuario rebaixarParaUser(Long usuarioId) { /* ... lógica ... */ return null; }
    public boolean isUsuarioAdmin(Long usuarioId) { return false; }
    public boolean isUsuarioAdmin(Usuario usuario) { return false; }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("TENTATIVA DE LOGIN para o usuário: {}", username);
        
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.warn("FALHA DE LOGIN: Usuário não encontrado com o email: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado com email: " + username);
                });

        // LOG DE DIAGNÓSTICO: Mostra a senha recuperada do banco na hora do login
        logger.info("USUÁRIO ENCONTRADO [{}]: Hash do banco de dados -> [{}]", username, usuario.getPassword());
        
        return usuario;
    }
}