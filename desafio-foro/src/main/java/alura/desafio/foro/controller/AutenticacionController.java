package alura.desafio.foro.controller;

import alura.desafio.foro.domain.usuario.DatosAutenticacion;
import alura.desafio.foro.domain.usuario.Usuario;
import alura.desafio.foro.repository.UsuarioRepository;
import alura.desafio.foro.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> autenticarUsuario(@RequestBody @Valid DatosAutenticacion datos) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                datos.email(), datos.contrasena());


        Authentication authentication = authenticationManager.authenticate(authToken);


        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();


        String jwtToken = tokenService.generarToken(usuarioAutenticado);


        var datosRespuesta = new DatosRespuestaLogin(jwtToken);

        return ResponseEntity.ok(datosRespuesta);
    }


    private record DatosRespuestaLogin(String token) {}
}