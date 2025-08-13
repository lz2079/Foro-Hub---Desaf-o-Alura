package alura.desafio.foro.domain.topico;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String mensaje;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    private String status;
    private String autor;
    private String curso;

    public Topico(DatosRegistroTopico datos) {
        this.titulo = datos.titulo();
        this.mensaje = datos.mensaje();
        this.fechaCreacion = LocalDateTime.now();
        this.status = "ACTIVO";
        this.autor = datos.autor();
        this.curso = datos.curso();
    }

    public void actualizarDatos(DatosActualizarTopico datos) {
        // Usamos ifPresent para actualizar solo si el Optional tiene un valor.
        datos.titulo().ifPresent(t -> this.titulo = t);
        datos.mensaje().ifPresent(m -> this.mensaje = m);
        datos.autor().ifPresent(a -> this.autor = a);
        datos.curso().ifPresent(c -> this.curso = c);
        // status y fechaCreacion no se actualizan aquí.
    }
}
