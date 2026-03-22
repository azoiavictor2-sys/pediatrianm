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
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha incorretos."));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("Usuário ou senha incorretos.");
        }

        String token = jwtUtil.generateToken(usuario.getUsername());

        return new LoginResponseDTO(token, usuario.getId(), usuario.getNomeCompleto(), usuario.getTurma());
    }
}
