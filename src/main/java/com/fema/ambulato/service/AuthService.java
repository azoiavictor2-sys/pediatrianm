package com.fema.ambulato.service;

import com.fema.ambulato.dto.LoginRequestDTO;
import com.fema.ambulato.dto.LoginResponseDTO;
import com.fema.ambulato.model.Usuario;
import com.fema.ambulato.repository.UsuarioRepository;
import com.fema.ambulato.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Usuario ou senha incorretos."));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("Usuario ou senha incorretos.");
        }

        // Gera um novo token
        String token = jwtUtil.generateToken(usuario.getUsername());

        // Salva o token como o unico ativo — invalida qualquer sessao anterior
        usuario.setTokenAtivo(token);
        usuarioRepository.save(usuario);

        return new LoginResponseDTO(token, usuario.getId(), usuario.getNomeCompleto(), usuario.getTurma());
    }

    /**
     * Verifica se o token enviado e o token ativo do usuario.
     * Se nao for, significa que outro dispositivo fez login depois.
     */
    public boolean isTokenAtivo(String username, String token) {
        return usuarioRepository.findByUsername(username)
                .map(u -> token.equals(u.getTokenAtivo()))
                .orElse(false);
    }

    /**
     * Invalida o token do usuario (logout).
     */
    public void logout(String username) {
        usuarioRepository.findByUsername(username).ifPresent(u -> {
            u.setTokenAtivo(null);
            usuarioRepository.save(u);
        });
    }
}