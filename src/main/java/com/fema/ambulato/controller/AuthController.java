package com.fema.ambulato.controller;

import com.fema.ambulato.model.Usuario;
import com.fema.ambulato.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository repository;

    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody Usuario tentativaLogin) {
        Optional<Usuario> usuarioEncontrado = repository.findByUsernameAndSenha(
                tentativaLogin.getUsername(),
                tentativaLogin.getSenha()
        );

        if (usuarioEncontrado.isPresent()) {
            return ResponseEntity.ok(usuarioEncontrado.get());
        } else {
            return ResponseEntity.status(401).body("Usuário ou senha incorretos.");
        }
    }

    // Cria os usuários automaticamente ao ligar o servidor
    @PostConstruct
    public void inicializarUsuarios() {
        // Verifica e cria o Victor
        if (repository.findByUsernameAndSenha("48473011880", "haloHAL2").isEmpty()) {
            Usuario u1 = new Usuario();
            u1.setUsername("48473011880");
            u1.setSenha("haloHAL2");
            u1.setNomeCompleto("Victor Iunglaus Azoia");
            u1.setTurma("T16");
            repository.save(u1);
        }

        // Verifica e cria a Maria Beatriz
        if (repository.findByUsernameAndSenha("45099722881", "19072002").isEmpty()) {
            Usuario u2 = new Usuario();
            u2.setUsername("45099722881");
            u2.setSenha("19072002");
            u2.setNomeCompleto("Maria Beatriz Paião de Camargo");
            u2.setTurma("T16");
            repository.save(u2);
        }

        // Verifica e cria o Daniel
        if (repository.findByUsernameAndSenha("38439895836", "Saori226886").isEmpty()) {
            Usuario u3 = new Usuario();
            u3.setUsername("38439895836");
            u3.setSenha("Saori226886");
            u3.setNomeCompleto("Daniel Dágola Dias");
            u3.setTurma("T16"); // Coloquei T16 como padrão, pode alterar se precisar
            repository.save(u3);
        }

        // Verifica e cria a Bianca
        if (repository.findByUsernameAndSenha("46381379898", "gb2520@t16").isEmpty()) {
            Usuario u4 = new Usuario();
            u4.setUsername("46381379898");
            u4.setSenha("gb2520@t16");
            u4.setNomeCompleto("Bianca Fongaro");
            u4.setTurma("T16");
            repository.save(u4);
        }

        // Verifica e cria a Bianca
        if (repository.findByUsernameAndSenha("43125267889", "123femafer").isEmpty()) {
            Usuario u4 = new Usuario();
            u4.setUsername("43125267889");
            u4.setSenha("123femafer");
            u4.setNomeCompleto("Fernanda Pobbe de Carvalho");
            u4.setTurma("T16");
            repository.save(u4);
        }

        // Verifica e cria a Bianca
        if (repository.findByUsernameAndSenha("42186039877", "Saga0901").isEmpty()) {
            Usuario u4 = new Usuario();
            u4.setUsername("42186039877");
            u4.setSenha("Saga0901");
            u4.setNomeCompleto("Sara Esther Bezerra Saqueto");
            u4.setTurma("T16");
            repository.save(u4);
        }

        // Verifica e cria a Bianca
        if (repository.findByUsernameAndSenha("46430976807", "Phfema10").isEmpty()) {
            Usuario u4 = new Usuario();
            u4.setUsername("46430976807");
            u4.setSenha("Phfema10");
            u4.setNomeCompleto("Pedro Henrique Gregorio");
            u4.setTurma("T16");
            repository.save(u4);
        }

        // Verifica e cria a Bianca
        if (repository.findByUsernameAndSenha("13075765910", "Andre101010").isEmpty()) {
            Usuario u4 = new Usuario();
            u4.setUsername("13075765910");
            u4.setSenha("Andre101010");
            u4.setNomeCompleto("André Luis Bragaglia Filho");
            u4.setTurma("T16");
            repository.save(u4);
        }

        // Verifica e cria a Bianca
        if (repository.findByUsernameAndSenha("43409319859", "Irys2209").isEmpty()) {
            Usuario u4 = new Usuario();
            u4.setUsername("43409319859");
            u4.setSenha("Irys2209");
            u4.setNomeCompleto("Irys Oliveira Dias");
            u4.setTurma("T16");
            repository.save(u4);
        }

        // Verifica e cria a Bianca
        if (repository.findByUsernameAndSenha("11820955931", "Daniel2006Z").isEmpty()) {
            Usuario u4 = new Usuario();
            u4.setUsername("11820955931");
            u4.setSenha("Daniel2006Z");
            u4.setNomeCompleto("Daniel Borges Torejani");
            u4.setTurma("T16");
            repository.save(u4);
        }

        // Verifica e cria a Bianca
        if (repository.findByUsernameAndSenha("46303350801", "H12dream").isEmpty()) {
            Usuario u4 = new Usuario();
            u4.setUsername("46303350801");
            u4.setSenha("H12dream");
            u4.setNomeCompleto("Victoria Harnisch da Silveira");
            u4.setTurma("T16");
            repository.save(u4);
        }

        System.out.println("Usuários verificados e carregados com sucesso!");
    }
}