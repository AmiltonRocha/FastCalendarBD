package unifor.calendario.fastcalendar.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unifor.calendario.fastcalendar.Model.Eventos;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.time.LocalDate;


@Repository
public interface EventosRepository extends JpaRepository<Eventos, Long> {
     
     //metodos de busca personalizadas
     Optional<Eventos> findByTitulo(String titulo);
     Optional<Eventos> findByResponsavel(String responsavel);
     Optional<Eventos> findByLocalidade(String localidade);
     Optional<Eventos> findByRecorrencia(Eventos.Recorrencia recorrencia);
     Optional<Eventos> findByRecorrenciaDiasSemana(String recorrenciaDiasSemana);
     Optional<Eventos> findByRecorrenciaFim(LocalDate recorrenciaFim);
     Optional<Eventos> findByCor(String cor);

}
