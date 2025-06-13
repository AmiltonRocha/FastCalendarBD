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
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "eventos")
public class Eventos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "titulo", nullable = false, length = 80)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_evento")
    private LocalDate dataEvento;

    @Column(name = "dias_da_semana", nullable = false)
    private String diasDaSemana;

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horario_fim", nullable = false)
    private LocalTime horarioFim;

    @Column(name = "responsavel")
    private String responsavel;

    @Column(name = "localidade", nullable = false, length = 80)
    private String localidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "recorrencia")
    private Recorrencia recorrencia;

    @Column(name = "recorrencia_dias_semana", length = 60)
    private String recorrenciaDiasSemana;

    @Column(name = "recorrencia_fim")
    private LocalDate recorrenciaFim;

    @Column(name = "cor", length = 10)
    private String cor;
    

    public enum Recorrencia {
        NENHUM,
        MENSAL,
        SEMESTRAL
    }
} 