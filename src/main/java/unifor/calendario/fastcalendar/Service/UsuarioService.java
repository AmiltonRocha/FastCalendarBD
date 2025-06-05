package unifor.calendario.fastcalendar.Service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import unifor.calendario.fastcalendar.Model.Usuario;
import unifor.calendario.fastcalendar.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService{
    private final PasswordEncoder passwordEncoder; // Injetar o PasswordEncoder


    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Métodos CRUD básicos (podem ser expandidos ou delegados diretamente do controller)
    public Usuario criarUsuario(Usuario usuario) {
        // Validações de negócio antes de salvar (ex: verificar se email/matrícula já existem)
        if (usuarioRepository.findByMatricula(usuario.getMatricula()).isPresent()) {
            throw new IllegalArgumentException("Matrícula já cadastrada: " + usuario.getMatricula());
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado: " + usuario.getEmail());
        }
        // Criptografar a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        // Por padrão, o cargo já é USER, conforme definido na entidade e construtor
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            // Lógica de atualização, verificando campos nulos e duplicidade se necessário
            if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().trim().isEmpty()) {
                usuario.setNome(usuarioAtualizado.getNome());
            }
            if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().trim().isEmpty()) {
                if (!usuario.getEmail().equals(usuarioAtualizado.getEmail()) &&
                        usuarioRepository.findByEmail(usuarioAtualizado.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Novo email '" + usuarioAtualizado.getEmail() + "' já cadastrado para outro usuário.");
                }
                usuario.setEmail(usuarioAtualizado.getEmail());
            }
            if (usuarioAtualizado.getMatricula() != null && !usuarioAtualizado.getMatricula().trim().isEmpty()) {
                 if (!usuario.getMatricula().equals(usuarioAtualizado.getMatricula()) &&
                        usuarioRepository.findByMatricula(usuarioAtualizado.getMatricula()).isPresent()) {
                    throw new IllegalArgumentException("Nova matrícula '" + usuarioAtualizado.getMatricula() + "' já cadastrada para outro usuário.");
                }
                usuario.setMatricula(usuarioAtualizado.getMatricula());
            }
            if (usuarioAtualizado.getDataNascimento() != null) {
                usuario.setDataNascimento(usuarioAtualizado.getDataNascimento());
            }
            if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().trim().isEmpty()) {
                // Criptografar a nova senha
                usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
            }
            // A atualização do cargo é feita por métodos específicos abaixo
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id + " para deleção.");
        }
        usuarioRepository.deleteById(id);
    }

    // Métodos específicos para gerenciamento de Cargos (Roles)
    public Usuario promoverParaAdmin(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + usuarioId));
        
        if (usuario.getCargo() == Usuario.Cargo.ADMIN) {
            throw new IllegalStateException("Usuário já é um administrador.");
        }
        
        usuario.setCargo(Usuario.Cargo.ADMIN);
        System.out.println("Usuário " + usuario.getNome() + " promovido para ADMIN.");
        return usuarioRepository.save(usuario);
    }

    public Usuario rebaixarParaUser(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + usuarioId));

        if (usuario.getCargo() == Usuario.Cargo.USER) {
            throw new IllegalStateException("Usuário já é um usuário comum (USER).");
        }

        usuario.setCargo(Usuario.Cargo.USER);
        System.out.println("Usuário " + usuario.getNome() + " rebaixado para USER.");
        return usuarioRepository.save(usuario);
    }

    public boolean isUsuarioAdmin(Long usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        return usuarioOpt.map(usuario -> usuario.getCargo() == Usuario.Cargo.ADMIN).orElse(false);
    }

    public boolean isUsuarioAdmin(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        return usuario.getCargo() == Usuario.Cargo.ADMIN;
    }

        // Implementação do método da interface UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // "username" aqui será o email, conforme definido em Usuario.getUsername()
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + username));
    }
}
