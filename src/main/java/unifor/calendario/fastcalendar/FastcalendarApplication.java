package unifor.calendario.fastcalendar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FastcalendarApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastcalendarApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("\n=================================================================");
            System.out.println(" Backend do FastCalendar iniciado com sucesso!");
            System.out.println(" A aplicacao esta pronta para receber requisicoes na porta 8080.");
            System.out.println(" Endpoints de Usuario: http://localhost:8080/usuarios");
            System.out.println(" Endpoints de Eventos: http://localhost:8080/eventos");
            System.out.println("=================================================================\n");
        };
    }
}