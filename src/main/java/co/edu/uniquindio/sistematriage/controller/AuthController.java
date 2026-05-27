package co.edu.uniquindio.sistematriage.controller;

import co.edu.uniquindio.sistematriage.domain.enums.RolUsuario;
import co.edu.uniquindio.sistematriage.domain.model.Usuario;
import co.edu.uniquindio.sistematriage.dto.request.LoginRequestDTO;
import co.edu.uniquindio.sistematriage.dto.request.UsuarioRegistroDTO;
import co.edu.uniquindio.sistematriage.dto.response.LoginResponseDTO;
import co.edu.uniquindio.sistematriage.exception.BusinessRuleException;
import co.edu.uniquindio.sistematriage.repository.UsuarioRepository;
import co.edu.uniquindio.sistematriage.security.JwtUtil;
import co.edu.uniquindio.sistematriage.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
            throw new BusinessRuleException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new BusinessRuleException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(
                usuario.getCorreo(),
                usuario.getRol().name(),
                usuario.getIdUsuario()
        );

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                usuario.getRol().name(),
                usuario.getIdUsuario().toString(),
                usuario.getNombre()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody UsuarioRegistroDTO dto) {
        Usuario nuevo = Usuario.builder()
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .password(dto.getPassword())
                .rol(dto.getRol())
                .build();

        usuarioService.registrarUsuario(nuevo);

        // Auto-login después del registro
        Usuario creado = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new BusinessRuleException("Error al registrar"));

        String token = jwtUtil.generateToken(
                creado.getCorreo(),
                creado.getRol().name(),
                creado.getIdUsuario()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDTO(
                token,
                creado.getRol().name(),
                creado.getIdUsuario().toString(),
                creado.getNombre()
        ));
    }
}