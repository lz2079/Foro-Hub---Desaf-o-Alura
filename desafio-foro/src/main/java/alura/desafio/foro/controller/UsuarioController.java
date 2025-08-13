package alura.desafio.foro.controller;

import alura.desafio.foro.domain.usuario.DatosRegistroUsuario;
import alura.desafio.foro.domain.usuario.Usuario;
import alura.desafio.foro.repository.UsuarioRepository;
import alura.desafio.foro.infra.exception.EmailDuplicadoException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    public ResponseEntity<DatosRespuestaUsuario> registrarUsuario(
            @RequestBody @Valid DatosRegistroUsuario datosRegistroUsuario,
            UriComponentsBuilder uriComponentsBuilder) {

        // Validar que no exista un usuario con el mismo email
        if (usuarioRepository.findByEmail(datosRegistroUsuario.email()).isPresent()) {
            throw new EmailDuplicadoException("Ya existe un usuario registrado con ese email.");
        }

        // Codificar la contraseña
        String contrasenaCodificada = passwordEncoder.encode(datosRegistroUsuario.contrasena());

        // Crear el usuario
        Usuario usuario = new Usuario(
                null,
                datosRegistroUsuario.nombre(),
                datosRegistroUsuario.email(),
                contrasenaCodificada
        );

        // Guardar el usuario
        usuarioRepository.save(usuario);

        // Crear datos de respuesta
        DatosRespuestaUsuario datosRespuestaUsuario = new DatosRespuestaUsuario(usuario);

        // Crear URI del nuevo recurso
        URI url = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();

        // Devolver 201 Created con la URI y los datos del usuario
        return ResponseEntity.created(url).body(datosRespuestaUsuario);
    }

    public record DatosRespuestaUsuario(Long id, String nombre, String email) {
        public DatosRespuestaUsuario(Usuario usuario) {
            this(usuario.getId(), usuario.getNombre(), usuario.getEmail());
        }
    }
}