package unifor.calendario.fastcalendar.Controller;
import unifor.calendario.fastcalendar.Model.Usuario; // importa a classe entidade Usuario
import unifor.calendario.fastcalendar.Service.UsuarioService; // Importa o serviço
import org.springframework.beans.factory.annotation.Autowired; // a injeção dedependências do Spring
import org.springframework.http.HttpStatus; // definir os códigos de status HTTP da resposta 
import org.springframework.http.ResponseEntity; //  construir respostas HTTP completas (status, corpo, headers)
import org.springframework.web.bind.annotation.*; //  todas as anotações REST (GetMapping, PostMapping, DeleteMapping, RequestBody, PathVariable)
import java.util.List; //  lidar com listas de usuários (no método de listar todos)
import java.util.Optional; // lidar com resultados que podem não existir (no método de buscar por ID)
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService; // Injeta o UsuarioService
    // private UsuarioRepository usuarioRepository; // Não é mais necessário injetar diretamente aqui para a maioria dos casos

     //metodo para poder cadastrar um usuario e salvar no banco de dados
    @PostMapping
    //primeiro nivel de validação para poder evitar erros de entrada de dados
    public ResponseEntity<Usuario> adicionarUsuario(@RequestBody Usuario usuario){
    if(usuario.getNome() == null || usuario.getNome().trim().isEmpty() ||
    usuario.getEmail() == null || usuario.getEmail().trim().isEmpty() ||
    usuario.getSenha() == null || usuario.getSenha().trim().isEmpty() ||
    usuario.getMatricula() == null || usuario.getMatricula().trim().isEmpty() ||
    usuario.getDataNascimento() == null){
        System.out.println("Erro: Dados incompletos");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    try{
        Usuario novoUsuario = usuarioService.criarUsuario(usuario); // Usa o serviço
        System.out.println("Usuário cadastrado com sucesso. ID: " + novoUsuario.getId());
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
        System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT); // Ou BAD_REQUEST dependendo da natureza do erro
    }catch(Exception e){
        // Captura qualquer outra exceção inesperada que possa ocorrer durante o processo de salvamento.
        System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);// erro 500

    }
}

@GetMapping// Mapeia requisições HTTP GET para o caminho base "/api/usuarios"
    public ResponseEntity<List<Usuario>> vizualizartodosUsuarios(){
    try{
        //Metodo usado para buscar todos os usuarios no banco de dados
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios(); // Usa o serviço

        if(usuarios.isEmpty()){
            //caso não tenha usuarios, o sistema retorna um status 204 (no content)
            System.out.println("Nenhum usuário encontrado");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        //caso tenho usuarios, o sistema retorna um status 200 (ok) e a lista de usuarios
        System.out.println("Lista de usuários encontrada com sucesso");
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }catch(Exception e){
        //captura qualquer outra exceção inesperada que possa ocorrer durante o processo de busca
        System.out.println("Erro interno do servidor ao buscar usuários: " + e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
//metodo para buscar um usuario pelo id
@GetMapping("/{id}")
public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id){
    Optional<Usuario> usuarioData = usuarioService.buscarUsuarioPorId(id); // Usa o serviço

    if(usuarioData.isPresent()){
        //caso encontre o usuario, o sistema retorna um status 200 (ok) e o usuario
        System.out.println("Usuário encontrado com ID:"+id);
        return new ResponseEntity<>(usuarioData.get(), HttpStatus.OK);
    }else{
        //caso não encontre o usuario, o sistema retorna um status 404 (not found)
        System.out.println("Usuário com ID " + id + " não encontrado.");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
//metodo para atualizar um usuario existente
@PutMapping("/{id}")
public ResponseEntity<Usuario> atualizarUsuario(@PathVariable("id") Long id, @RequestBody Usuario usuarioAtualizado) {
    try {
        Usuario usuarioSalvo = usuarioService.atualizarUsuario(id, usuarioAtualizado); // Usa o serviço
        System.out.println("Usuário com ID " + id + " atualizado com sucesso.");
        return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
        System.out.println("Erro ao atualizar usuário: " + e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT); // Ou BAD_REQUEST
    } catch (RuntimeException e) { // Captura o "Usuário não encontrado" do serviço
        System.out.println("Usuário com ID " + id + " não encontrado para atualização.");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
//metodo para deletar um usuario pelo id
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletarUsuario(@PathVariable("id") Long id){
    try{
        usuarioService.deletarUsuario(id); // Usa o serviço
        System.out.println("Usuário com ID " + id + " deletado com sucesso.");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (RuntimeException e) { // Captura o "Usuário não encontrado" do serviço
        System.err.println("Erro ao deletar usuário com ID " + id + ": " + e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}catch(Exception e){
    //caso haja erro durante a remoção 
    System.err.println("Erro interno do servidor ao deletar usuário com ID " + id + ": " + e.getMessage());
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);// retorna erro 500 (esse demonio)

}

}

    // Endpoints para gerenciar cargos
    @PostMapping("/{id}/promoverAdmin")
    public ResponseEntity<Usuario> promoverParaAdmin(@PathVariable Long id) {
        try {
            Usuario usuarioAdmin = usuarioService.promoverParaAdmin(id);
            return ResponseEntity.ok(usuarioAdmin);
        } catch (RuntimeException e) {
            System.out.println("Erro ao promover usuário: " + e.getMessage());
            // Pode ser NOT_FOUND se o usuário não existe, ou BAD_REQUEST/CONFLICT se já é admin
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Simplificado
        }
    }

    @PostMapping("/{id}/rebaixarUser")
    public ResponseEntity<Usuario> rebaixarParaUser(@PathVariable Long id) {
        try {
            Usuario usuarioUser = usuarioService.rebaixarParaUser(id);
            return ResponseEntity.ok(usuarioUser);
        } catch (RuntimeException e) {
            System.out.println("Erro ao rebaixar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Simplificado
        }
    }

// @DeleteMapping
//     public ResponseEntity<HttpStatus> deletarTodosUsuarios() {
//         try {
//             // Usa o método `deleteAll()` do JpaRepository para remover TODOS os registros da tabela.
//             usuarioRepository.deleteAll();
//             System.out.println("Todos os usuários foram deletados.");
//             return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//         } catch (Exception e) {
//             System.err.println("Erro interno do servidor ao deletar todos os usuários: " + e.getMessage());
//             return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     }

}
