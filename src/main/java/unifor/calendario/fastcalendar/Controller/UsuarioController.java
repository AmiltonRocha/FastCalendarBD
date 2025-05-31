package unifor.calendario.fastcalendar.Controller;
import unifor.calendario.fastcalendar.Model.Usuario; // importa a classe entidade Usuario
import unifor.calendario.fastcalendar.Repository.UsuarioRepository; // importa a interface do repositório
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
    private UsuarioRepository usuarioRepository;


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
    //caso uma matricula seja igual a outra, o sistema não permitirá o cadastro
    if(usuarioRepository.findByMatricula(usuario.getMatricula()).isPresent()){
        System.out.println("Erro: Matrícula já cadastrada");
        return new ResponseEntity<>(HttpStatus.CONFLICT);

    }
    try{
            // O método `save()` do JpaRepository:
            // - Se o `usuario.id` for `null` (como é o caso de um novo usuário), ele executa um SQL INSERT.
            // - Se o `usuario.id` tiver um valor e esse ID já existir no banco, ele executa um SQL UPDATE.
            // O objeto retornado (`novoUsuarioSalvo`) conterá o ID gerado pelo banco.

        Usuario novoUsuario = usuarioRepository.save(usuario);
        System.out.println("Usuário cadastrado com sucesso. ID: " + novoUsuario.getId());
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);




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
        List<Usuario> usuarios = usuarioRepository.findAll();

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
    Optional<Usuario> usuarioData = usuarioRepository.findById(id);

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
    //vamos ver se a usuario existente
    Optional<Usuario> usuarioExistenteData = usuarioRepository.findById(id);

    if(usuarioExistenteData.isPresent()){
        Usuario _usuario = usuarioExistenteData.get();//pega o usuario existente
        if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().trim().isEmpty()) {
            _usuario.setNome(usuarioAtualizado.getNome());
        }
        if(usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().trim().isEmpty()){
            //verifica se o email é diferente e se já existe
            if(!_usuario.getEmail().equals(usuarioAtualizado.getEmail()) &&
            usuarioRepository.findByEmail(usuarioAtualizado.getEmail()).isPresent()){
                System.out.println("Erro: Novo email '" + usuarioAtualizado.getEmail() + "' já cadastrado para outro usuário.");
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            _usuario.setEmail(usuarioAtualizado.getEmail());
        }

        if(usuarioAtualizado.getMatricula() != null && !usuarioAtualizado.getMatricula().trim().isEmpty()){
            //verifica se a matricula é diferente e se já existe
            if(!_usuario.getMatricula().equals(usuarioAtualizado.getMatricula()) &&
            usuarioRepository.findByMatricula(usuarioAtualizado.getMatricula()).isPresent()){
                System.out.println("Erro: Nova matrícula '" + usuarioAtualizado.getMatricula() + "' já cadastrada para outro usuário.");
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            _usuario.setMatricula(usuarioAtualizado.getMatricula());
        }
        if(usuarioAtualizado.getDataNascimento() != null){
            _usuario.setDataNascimento(usuarioAtualizado.getDataNascimento());
        }
        Usuario usuarioSalvo = usuarioRepository.save(_usuario);
        System.out.println("Usuário com ID " + id + " atualizado com sucesso.");
        return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);
    } else {
        // Se o usuário com o ID fornecido não for encontrado, retorna 404 Not Found.
        System.out.println("Usuário com ID " + id + " não encontrado para atualização.");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
//metodo para deletar um usuario pelo id
@DeleteMapping("/{id}")
public ResponseEntity<Void> deletarUsuario(@PathVariable("id") Long id){
    try{
        //vou verificar se o usuario existe
        if(!usuarioRepository.existsById(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    usuarioRepository.deleteById(id);
    System.out.println("Usuário com ID " + id + " deletado com sucesso.");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
}catch(Exception e){
    //caso haja erro durante a remoção 
    System.err.println("Erro interno do servidor ao deletar usuário com ID " + id + ": " + e.getMessage());
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);// retorna erro 500 (esse demonio)

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




