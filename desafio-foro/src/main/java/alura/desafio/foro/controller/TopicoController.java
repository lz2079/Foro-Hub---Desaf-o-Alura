package alura.desafio.foro.controller;

import alura.desafio.foro.domain.topico.DatosActualizarTopico;
import alura.desafio.foro.domain.topico.DatosListadoTopico;
import alura.desafio.foro.domain.topico.DatosRegistroTopico;
import alura.desafio.foro.domain.topico.Topico;
import alura.desafio.foro.repository.TopicoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DatosRespuestaTopico> registrarTopico(
            @RequestBody @Valid DatosRegistroTopico datosRegistroTopico,
            UriComponentsBuilder uriComponentsBuilder) {

        // Validar que no exista un tópico con el mismo título y mensaje
        if (topicoRepository.existsByTituloAndMensaje(datosRegistroTopico.titulo(), datosRegistroTopico.mensaje())) {
            return ResponseEntity.badRequest().build();
        }

        Topico topico = new Topico(datosRegistroTopico);
        topicoRepository.save(topico);

        DatosRespuestaTopico datosRespuestaTopico = new DatosRespuestaTopico(topico);

        URI url = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaTopico);
    }

    // --- Método actualizarTopico CORREGIDO ---
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosRespuestaTopico> actualizarTopico(
            @PathVariable Long id, // Captura el ID de la URL
            @RequestBody @Valid DatosActualizarTopico datosActualizarTopico) {


        Topico topico = topicoRepository.getReferenceById(id);

        topico.actualizarDatos(datosActualizarTopico);


        DatosRespuestaTopico datosRespuestaTopico = new DatosRespuestaTopico(topico);
        return ResponseEntity.ok(datosRespuestaTopico);
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listadoTopicos(
            @PageableDefault(size = 10, sort = "fechaCreacion") Pageable paginacion) {
        return ResponseEntity.ok(
                topicoRepository.findAll(paginacion)
                        .map(DatosListadoTopico::new)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaTopico> retornaDatosTopico(@PathVariable Long id) {
        Topico topico = topicoRepository.getReferenceById(id);
        var datosTopico = new DatosRespuestaTopico(topico);
        return ResponseEntity.ok(datosTopico);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarTopico(@PathVariable Long id) {
        topicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
