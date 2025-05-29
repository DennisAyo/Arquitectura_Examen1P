package com.banquito.sistema.originacion.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 255, message = "El nombre del producto no puede tener más de 255 caracteres")
    @Column(name = "nombre_producto", nullable = false, length = 255)
    private String nombreProducto;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio de venta debe tener máximo 8 dígitos enteros y 2 decimales")
    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @DecimalMin(value = "0.0", inclusive = false, message = "El costo de compra debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El costo de compra debe tener máximo 8 dígitos enteros y 2 decimales")
    @Column(name = "costo_compra", precision = 10, scale = 2)
    private BigDecimal costoCompra;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Pattern(regexp = "ACTIVO|INACTIVO|AGOTADO", message = "El estado debe ser ACTIVO, INACTIVO o AGOTADO")
    @Column(name = "estado_producto", length = 20, nullable = false)
    private String estadoProducto = "ACTIVO";

    @NotNull(message = "La categoría del producto es obligatoria")
    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", insertable = false, updatable = false)
    private CategoriaProducto categoria;

    @Version
    @Column(name = "version")
    private Long version;

    public Producto(Long idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(idProducto, producto.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto);
    }

    @PrePersist
    protected void onCreate() {
        if (estadoProducto == null || estadoProducto.trim().isEmpty()) {
            estadoProducto = "ACTIVO";
        }
        if (stockActual != null && stockActual == 0) {
            estadoProducto = "AGOTADO";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (stockActual != null && stockActual == 0 && "ACTIVO".equals(estadoProducto)) {
            estadoProducto = "AGOTADO";
        }
    }
} 