package unifor.calendario.fastcalendar.Controller;
import unifor.calendario.fastcalendar.Model.Eventos; // importa a classe entidade Usuario
import unifor.calendario.fastcalendar.Repository.EventosRepository; // importa a interface do repositório
import org.springframework.beans.factory.annotation.Autowired; // a injeção dedependências do Spring
import org.springframework.http.HttpStatus; // definir os códigos de status HTTP da resposta 
import org.springframework.http.ResponseEntity; //  construir respostas HTTP completas (status, corpo, headers)
import org.springframework.web.bind.annotation.*; //  todas as anotações REST (GetMapping, PostMapping, DeleteMapping, RequestBody, PathVariable)
import java.util.List; //  lidar com listas de usuários (no método de listar todos)
import java.util.Optional; // lidar com resultados que podem não existir (no método de buscar por ID)
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/eventos") // define o endpoint para acessar os eventos
public class EventosController {
    @Autowired
    private EventosRepository eventosRepository; // injeção de dependência do Spring

    @PostMapping // cria um novo evento
    public ResponseEntity<Eventos> criarEvento(@RequestBody Eventos evento){
        Eventos novoEvento = eventosRepository.save(evento);
        System.out.println("Evento criado com sucesso: " + novoEvento);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
    }
    
    @GetMapping // lista todos os eventos
    public ResponseEntity<List<Eventos>> listarTodosEventos(){
        //busca todos os eventos
        List<Eventos> eventos = eventosRepository.findAll(); // busca todos os eventos
        System.out.println("Lista de eventos: " + eventos); 
        return new ResponseEntity<>(eventos, HttpStatus.OK); // retorna a lista de eventos
    }

   @GetMapping("/{id}") // busca um evento pelo ID
   public ResponseEntity<Eventos> buscarEventoPorId(@PathVariable Long id){
    //busca o evento pelo ID
    Optional<Eventos> evento = eventosRepository.findById(id);//busca o evento pelo ID
    System.out.println("Evento encontrado: " + evento);
    return evento.map(e -> new ResponseEntity<>(e, HttpStatus.OK))//retorna o evento encontrado 
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));//retorna um erro 404 se o evento não for encontrado
   }

   @PutMapping("/{id}") // atualiza um evento existente
   public ResponseEntity<Eventos> autalizarEventos(@PathVariable Long id, @RequestBody Eventos eventosAtualizados){
        //verifica se o evento existe
        Optional<Eventos> eventoExistente = eventosRepository.findById(id);//busca o evento pelo ID
        if(eventoExistente.isPresent()){
            //Se existir, pega o evento e atualiza seus campos com os dados da requisição..
            Eventos evento = eventoExistente.get();
            evento.setTitulo(eventosAtualizados.getTitulo());
            evento.setDescricao(eventosAtualizados.getDescricao());
            evento.setDiasDaSemana(eventosAtualizados.getDiasDaSemana());
            evento.setHorarioInicio(eventosAtualizados.getHorarioInicio());
            evento.setHorarioFim(eventosAtualizados.getHorarioFim());
            evento.setResponsavel(eventosAtualizados.getResponsavel());
            evento.setLocalidade(eventosAtualizados.getLocalidade());
            evento.setRecorrencia(eventosAtualizados.getRecorrencia());
            evento.setRecorrenciaDiasSemana(eventosAtualizados.getRecorrenciaDiasSemana());
            evento.setRecorrenciaFim(eventosAtualizados.getRecorrenciaFim());
            evento.setCor(eventosAtualizados.getCor());
            
            // Salva as alterações (o método save do JpaRepository atualiza se o ID existe)
            Eventos eventoSalvo = eventosRepository.save(evento);
            System.out.println("Evento atualizado com sucesso: " + eventoSalvo);
            // Retorna o evento atualizado com status 200 OK
            return new ResponseEntity<>(eventoSalvo, HttpStatus.OK);
        } else {
            // Se o evento não for encontrado, retorna 404 Not Found
            System.out.println("Evento não encontrado");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}") // deleta um evento existente
    public ResponseEntity<Void> deletarEvento(@PathVariable Long id){
        //verifica se o evento existe
        if(eventosRepository.existsById(id)){
            eventosRepository.deleteById(id);
            //retorna um status 204 No Content
            System.out.println("Evento deletado com sucesso");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            //retorna um status 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Metodos de busca personalizadas
    @GetMapping("/titulo/{titulo}")//busca eventos por titulo
    public ResponseEntity<Eventos> buscarPorTitulo(@PathVariable String titulo){//busca o evento pelo titulo
        Optional<Eventos> evento = eventosRepository.findByTitulo(titulo);//busca o evento pelo titulo
        if(evento.isPresent()){//se o evento for encontrado
            System.out.println("Evento encontrado: " + evento.get());
            return new ResponseEntity<>(evento.get(), HttpStatus.OK);
        }
        else{//se o evento não for encontrado
            System.out.println("Evento não encontrado");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
}