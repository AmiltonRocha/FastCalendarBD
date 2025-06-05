package unifor.calendario.fastcalendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import unifor.calendario.fastcalendar.Repository.UsuarioRepository;
import unifor.calendario.fastcalendar.Model.Usuario;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class FastcalendarApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastcalendarApplication.class, args);
	}

	@Bean
	CommandLineRunner demoCrud(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){ // Injetar PasswordEncoder
		return (args) -> {
			try {
				System.out.println("\n--- INICIANDO DEMONSTRAÇÃO CRUD COM SPRING BOOT ---");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date data1 = sdf.parse("1990-01-01");
				Date data2 = sdf.parse("1995-05-15");
				Date data3 = sdf.parse("1992-03-20");

				//criando um usuario
				System.out.println("Tentando salvar o primeiro usuário...");
				Usuario usuario1 = usuarioRepository.save(new Usuario("João Amorin", "joao.amorin2@example.com", passwordEncoder.encode("1234567890"), "1990-01-01", data1));
				System.out.println("Primeiro usuário salvo com ID: " + usuario1.getId());

				System.out.println("Tentando salvar o segundo usuário...");
				Usuario usuario2 = usuarioRepository.save(new Usuario("Isaac Newton", "Isaac.newton@example.com", passwordEncoder.encode("9876543210"), "1995-05-15", data2));
				System.out.println("Segundo usuário salvo com ID: " + usuario2.getId());

				System.out.println("Tentando salvar o terceiro usuário...");
				Usuario usuario3 = usuarioRepository.save(new Usuario("LopesManeiro", "LopesManeiro@example.com", passwordEncoder.encode("9876543210"), "1992-03-20", data3));
				System.out.println("Terceiro usuário salvo com ID: " + usuario3.getId());


			 
				//ler usuarios
				System.out.println("\n[READ] Buscando todos os usuários...");
				List<Usuario> todosUsuarios = usuarioRepository.findAll();
				if (todosUsuarios.isEmpty()) {
					System.out.println("Nenhum usuário encontrado.");
				} else {
					System.out.println("Número de usuários encontrados: " + todosUsuarios.size());
					todosUsuarios.forEach(System.out::println);
				}

				//ler usuario por id
				System.out.println("\n[READ] Buscando usuário por ID...");
				Optional<Usuario> usuarioEncontrado = usuarioRepository.findById(usuario1.getId());
				if (usuarioEncontrado.isPresent()) {
					System.out.println("Usuário encontrado: " + usuarioEncontrado.get());
				} else {
					System.out.println("Nenhum usuário encontrado.");
				}

				//atualizar usuario
				System.out.println("\n[UPDATE] Atualizando usuário...");
				usuarioEncontrado.get().setNome("João Amorin Atualizado");
				usuarioEncontrado.get().setEmail("joao.amorin2@example.com");
				usuarioEncontrado.get().setMatricula("1990-01-01");
				usuarioEncontrado.get().setDataNascimento(data1);	
				Usuario usuarioAtualizado = usuarioRepository.save(usuarioEncontrado.get());
				System.out.println("Usuário atualizado: " + usuarioAtualizado);

				//deletar usuario
			// 	Long idParaDeletar = usuario2.getId();
			// 	if (idParaDeletar != null) {
			// 		System.out.println("\n[DELETE] Deletando usuário com ID: " + idParaDeletar);
			// 		if (usuarioRepository.existsById(idParaDeletar)) {
			// 			usuarioRepository.deleteById(idParaDeletar);
			// 			System.out.println("Usuário com ID " + idParaDeletar + " deletado.");
			// 		} else {
			// 			System.out.println("Usuário com ID " + idParaDeletar + " não encontrado para deleção.");
			// 		}
			// 	}

			// 	System.out.println("\n[VERIFICAÇÃO PÓS-DELETE] Buscando todos os usuários novamente...");
			// 	todosUsuarios = usuarioRepository.findAll();
			// 	if (todosUsuarios.isEmpty()) {
			// 		System.out.println("Nenhum usuário encontrado.");
			// 	} else {
			// 		todosUsuarios.forEach(System.out::println);
			// 	}

			// 	System.out.println("\n--- DEMONSTRAÇÃO CRUD FINALIZADA ---");
			} catch (Exception e) {
				System.err.println("Erro durante a execução do CRUD: " + e.getMessage());
				e.printStackTrace();
			}
		};
	}

}
