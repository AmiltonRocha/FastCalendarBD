package unifor.calendario.fastcalendar.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import unifor.calendario.fastcalendar.Model.Usuario;
import unifor.calendario.fastcalendar.Service.UsuarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint de "Login" para o Frontend.
     * Retorna os dados do usuário autenticado no momento.
     * O frontend deve chamar esta rota com Basic Auth para verificar as credenciais e obter os dados do usuário.
     */
    @GetMapping("/me")
    public ResponseEntity<Usuario> getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        // Criamos uma resposta sem a senha por segurança
        Usuario resposta = new Usuario();
        resposta.setId(usuarioLogado.getId());
        resposta.setNome(usuarioLogado.getNome());
        resposta.setEmail(usuarioLogado.getEmail());
        resposta.setMatricula(usuarioLogado.getMatricula());
        resposta.setDataNascimento(usuarioLogado.getDataNascimento());
        resposta.setCargo(usuarioLogado.getCargo());

        return ResponseEntity.ok(resposta);
    }

    @PostMapping
    public ResponseEntity<Usuario> adicionarUsuario(@RequestBody Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty() ||
            usuario.getEmail() == null || usuario.getEmail().trim().isEmpty() ||
            usuario.getSenha() == null || usuario.getSenha().trim().isEmpty() ||
            usuario.getMatricula() == null || usuario.getMatricula().trim().isEmpty() ||
            usuario.getDataNascimento() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Usuario novoUsuario = usuarioService.criarUsuario(usuario);
            return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> vizualizartodosUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuarioData = usuarioService.buscarUsuarioPorId(id);
        return usuarioData.map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable("id") Long id, @RequestBody Usuario usuarioAtualizado) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        // VERIFICAÇÃO DE SEGURANÇA: Permite a ação se o usuário for ADMIN ou o dono da conta.
        if (usuarioLogado.getCargo() != Usuario.Cargo.ADMIN && !usuarioLogado.getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Proibido
        }

        try {
            Usuario usuarioSalvo = usuarioService.atualizarUsuario(id, usuarioAtualizado);
            return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable("id") Long id) {
        try {
            usuarioService.deletarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/promoverAdmin")
    public ResponseEntity<Usuario> promoverParaAdmin(@PathVariable Long id) {
        try {
            Usuario usuarioAdmin = usuarioService.promoverParaAdmin(id);
            return ResponseEntity.ok(usuarioAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/{id}/rebaixarUser")
    public ResponseEntity<Usuario> rebaixarParaUser(@PathVariable Long id) {
        try {
            Usuario usuarioUser = usuarioService.rebaixarParaUser(id);
            return ResponseEntity.ok(usuarioUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}