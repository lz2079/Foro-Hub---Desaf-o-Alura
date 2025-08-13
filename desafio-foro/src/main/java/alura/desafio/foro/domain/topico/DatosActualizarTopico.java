package alura.desafio.foro.domain.topico;

import java.util.Optional;


public record DatosActualizarTopico(
        Optional<String> titulo,
        Optional<String> mensaje,
        Optional<String> autor,
        Optional<String> curso

) {

    public DatosActualizarTopico(String titulo, String mensaje, String autor, String curso) {
        this(Optional.ofNullable(titulo), Optional.ofNullable(mensaje), Optional.ofNullable(autor), Optional.ofNullable(curso));
    }
}