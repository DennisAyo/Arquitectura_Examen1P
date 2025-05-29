package com.banquito.sistema.originacion.model;

import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "categoria_producto")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre de la categoría no puede tener más de 100 caracteres")
    @Column(name = "nombre_categoria", nullable = false, length = 100, unique = true)
    private String nombreCategoria;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Version
    @Column(name = "version")
    private Long version;

    public CategoriaProducto(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaProducto that = (CategoriaProducto) o;
        return Objects.equals(idCategoria, that.idCategoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategoria);
    }
} 