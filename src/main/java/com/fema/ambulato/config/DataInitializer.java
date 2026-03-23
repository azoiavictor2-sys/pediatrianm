package com.fema.ambulato.config;

import com.fema.ambulato.model.Usuario;
import com.fema.ambulato.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Inicializa os usuários do sistema na primeira execução.
 * As credenciais são lidas do arquivo externo 'usuarios.properties'
 * que NÃO deve ser versionado no git.
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
        File externalFile = new File("usuarios.properties");

        if (!externalFile.exists()) {
            System.out.println("⚠️  DataInitializer: arquivo 'usuarios.properties' não encontrado. Nenhum usuário foi criado.");
            System.out.println("    Crie o arquivo 'usuarios.properties' na raiz do projeto com as credenciais dos usuários.");
            return;
        }

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(externalFile));

            int index = 0;
            while (props.containsKey("usuarios[" + index + "].cpf")) {
                String cpf    = props.getProperty("usuarios[" + index + "].cpf");
                String senha  = props.getProperty("usuarios[" + index + "].senha");
                String nome   = props.getProperty("usuarios[" + index + "].nome");
                String turma  = props.getProperty("usuarios[" + index + "].turma", "T16");
                criarUsuarioSeNaoExistir(cpf, senha, nome, turma);
                index++;
            }

            System.out.println("✅ DataInitializer: " + index + " usuário(s) verificado(s) e carregado(s) com sucesso!");

        } catch (IOException e) {
            System.err.println("❌ DataInitializer: erro ao ler 'usuarios.properties' — " + e.getMessage());
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
            System.out.println("   → Usuário criado: " + nomeCompleto);
        }
    }
}
