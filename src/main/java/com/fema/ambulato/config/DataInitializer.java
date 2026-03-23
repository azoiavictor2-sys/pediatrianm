package com.fema.ambulato.config;

import com.fema.ambulato.model.Usuario;
import com.fema.ambulato.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        File externalFile = new File("usuarios.properties");

        if (!externalFile.exists()) {
            System.out.println("  DataInitializer: arquivo 'usuarios.properties' nao encontrado.");
            return;
        }

        try {
            Properties props = new Properties();
            // CORRECAO: Leitura em UTF-8 para suportar acentos nos nomes
            props.load(new InputStreamReader(new FileInputStream(externalFile), StandardCharsets.UTF_8));

            int index = 0;
            while (props.containsKey("usuarios[" + index + "].cpf")) {
                String cpf = props.getProperty("usuarios[" + index + "].cpf");
                String senha = props.getProperty("usuarios[" + index + "].senha");
                String nome = props.getProperty("usuarios[" + index + "].nome");
                String turma = props.getProperty("usuarios[" + index + "].turma", "T16");
                criarUsuarioSeNaoExistir(cpf, senha, nome, turma);
                index++;
            }

            System.out.println("  DataInitializer: " + index + " usuario(s) verificado(s) com sucesso!");

        } catch (IOException e) {
            System.err.println("  DataInitializer: erro ao ler 'usuarios.properties' - " + e.getMessage());
        }
    }

    private void criarUsuarioSeNaoExistir(String username, String senhaPlana, String nomeCompleto, String turma) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setSenha(passwordEncoder.encode(senhaPlana));
            u.setNomeCompleto(nomeCompleto);
            u.setTurma(turma);
            usuarioRepository.save(u);
            System.out.println("   -> Usuario criado: " + nomeCompleto);
        }
    }
}