package com.fema.ambulato.config;

import com.fema.ambulato.model.Usuario;
import com.fema.ambulato.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializa os usuários do sistema na primeira execução.
 * As senhas são armazenadas criptografadas com BCrypt.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        criarUsuarioSeNaoExistir("48473011880", "haloHAL2", "Victor Iunglaus Azoia", "T16");
        criarUsuarioSeNaoExistir("45099722881", "19072002", "Maria Beatriz Paião de Camargo", "T16");
        criarUsuarioSeNaoExistir("38439895836", "Saori226886", "Daniel Dágola Dias", "T16");
        criarUsuarioSeNaoExistir("46381379898", "gb2520@t16", "Bianca Fongaro", "T16");
        criarUsuarioSeNaoExistir("43125267889", "123femafer", "Fernanda Pobbe de Carvalho", "T16");
        criarUsuarioSeNaoExistir("42186039877", "Saga0901", "Sara Esther Bezerra Saqueto", "T16");
        criarUsuarioSeNaoExistir("46430976807", "Phfema10", "Pedro Henrique Gregorio", "T16");
        criarUsuarioSeNaoExistir("13075765910", "Andre101010", "André Luis Bragaglia Filho", "T16");
        criarUsuarioSeNaoExistir("43409319859", "Irys2209", "Irys Oliveira Dias", "T16");
        criarUsuarioSeNaoExistir("11820955931", "Daniel2006Z", "Daniel Borges Torejani", "T16");
        criarUsuarioSeNaoExistir("46303350801", "H12dream", "Victoria Harnisch da Silveira", "T16");

        System.out.println("✅ DataInitializer: usuários verificados e carregados com sucesso!");
    }

    private void criarUsuarioSeNaoExistir(String username, String senhaPlana, String nomeCompleto, String turma) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setSenha(passwordEncoder.encode(senhaPlana)); // Senha criptografada com BCrypt
            u.setNomeCompleto(nomeCompleto);
            u.setTurma(turma);
            usuarioRepository.save(u);
            System.out.println("   → Usuário criado: " + nomeCompleto);
        }
    }
}
