package com.fema.ambulato.config;

import com.fema.ambulato.model.Usuario;
import com.fema.ambulato.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${AMBULATO_USERS:}")
    private String usuariosEnv;

    @Override
    public void run(String... args) {
        // Tenta ler do arquivo local primeiro (desenvolvimento)
        File externalFile = new File("usuarios.properties");
        if (externalFile.exists()) {
            carregarDoArquivo(externalFile);
        }

        // Tenta ler da variavel de ambiente (producao/Railway)
        if (usuariosEnv != null && !usuariosEnv.isEmpty()) {
            carregarDaVariavel(usuariosEnv);
        }
    }

    private void carregarDoArquivo(File arquivo) {
        try {
            Properties props = new Properties();
            props.load(new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8));

            int index = 0;
            while (props.containsKey("usuarios[" + index + "].cpf")) {
                String cpf = props.getProperty("usuarios[" + index + "].cpf");
                String senha = props.getProperty("usuarios[" + index + "].senha");
                String nome = props.getProperty("usuarios[" + index + "].nome");
                String turma = props.getProperty("usuarios[" + index + "].turma", "T16");
                criarUsuarioSeNaoExistir(cpf, senha, nome, turma);
                index++;
            }
            System.out.println("DataInitializer: " + index + " usuario(s) do arquivo verificados.");
        } catch (IOException e) {
            System.err.println("DataInitializer: erro ao ler arquivo - " + e.getMessage());
        }
    }

    private void carregarDaVariavel(String dados) {
        String[] usuarios = dados.split(";");
        int count = 0;
        for (String u : usuarios) {
            String[] campos = u.trim().split(",");
            if (campos.length >= 3) {
                String cpf = campos[0].trim();
                String senha = campos[1].trim();
                String nome = campos[2].trim();
                String turma = campos.length >= 4 ? campos[3].trim() : "T16";
                criarUsuarioSeNaoExistir(cpf, senha, nome, turma);
                count++;
            }
        }
        System.out.println("DataInitializer: " + count + " usuario(s) da variavel verificados.");
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