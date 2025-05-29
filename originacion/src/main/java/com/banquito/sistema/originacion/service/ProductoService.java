package com.banquito.sistema.originacion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.sistema.originacion.exception.InvalidStateException;
import com.banquito.sistema.originacion.exception.NotFoundException;
import com.banquito.sistema.originacion.model.Producto;
import com.banquito.sistema.originacion.repository.ProductoRepository;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository repository;
    private final CategoriaProductoService categoriaProductoService;

    public ProductoService(ProductoRepository repository, CategoriaProductoService categoriaProductoService) {
        this.repository = repository;
        this.categoriaProductoService = categoriaProductoService;
    }

    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        Optional<Producto> producto = this.repository.findById(id);
        if (producto.isEmpty()) {
            throw new NotFoundException(id.toString(), "Producto");
        }
        return producto.get();
    }

    @Transactional(readOnly = true)
    public List<Producto> findByEstado(String estado) {
        return this.repository.findByEstadoProducto(estado);
    }

    @Transactional(readOnly = true)
    public List<Producto> findByCategoria(Long idCategoria) {
        return this.repository.findByIdCategoria(idCategoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> findByNombre(String nombreProducto) {
        return this.repository.findByNombreProductoContainingIgnoreCase(nombreProducto);
    }

    @Transactional(readOnly = true)
    public List<Producto> findByEstadoAndCategoria(String estado, Long idCategoria) {
        return this.repository.findByEstadoProductoAndIdCategoria(estado, idCategoria);
    }

    @Transactional(readOnly = true)
    public List<Producto> findByStockBajo(Integer stockMinimo) {
        return this.repository.findByStockBajo(stockMinimo);
    }

    @Transactional(readOnly = true)
    public List<Producto> findProductosAgotados() {
        return this.repository.findProductosAgotados();
    }

    public Producto create(Producto producto) {
        this.validateForCreate(producto);
        
        // Validar que la categoría existe
        this.categoriaProductoService.findById(producto.getIdCategoria());
        
        // Establecer estado inicial
        if (producto.getEstadoProducto() == null || producto.getEstadoProducto().trim().isEmpty()) {
            producto.setEstadoProducto("ACTIVO");
        }
        
        // Si el stock es 0, marcar como agotado
        if (producto.getStockActual() != null && producto.getStockActual() == 0) {
            producto.setEstadoProducto("AGOTADO");
        }
        
        return this.repository.save(producto);
    }

    public Producto changeState(Long id, String newState, String motivo) {
        Producto producto = this.findById(id);
        String oldState = producto.getEstadoProducto();
        
        this.validateStateChange(oldState, newState);
        
        producto.setEstadoProducto(newState);
        return this.repository.save(producto);
    }

    public Producto aumentarStock(Long id, Integer cantidad, BigDecimal precioCompra) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (precioCompra == null || precioCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser mayor a 0");
        }
        
        Producto producto = this.findById(id);
        
        // Aumentar stock
        Integer nuevoStock = producto.getStockActual() + cantidad;
        producto.setStockActual(nuevoStock);
        
        // Actualizar costo de compra
        producto.setCostoCompra(precioCompra);
        
        // Calcular nuevo precio de venta (costo + 25%)
        BigDecimal nuevoPrecioVenta = precioCompra.multiply(new BigDecimal("1.25"))
                                                  .setScale(2, RoundingMode.HALF_UP);
        producto.setPrecioVenta(nuevoPrecioVenta);
        
        // Cambiar estado a ACTIVO
        producto.setEstadoProducto("ACTIVO");
        
        return this.repository.save(producto);
    }

    public Producto disminuirStock(Long id, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        
        Producto producto = this.findById(id);
        
        // Validar que hay suficiente stock
        if (producto.getStockActual() < cantidad) {
            throw new IllegalArgumentException(
                String.format("Stock insuficiente. Stock actual: %d, cantidad solicitada: %d", 
                             producto.getStockActual(), cantidad)
            );
        }
        
        // Disminuir stock
        Integer nuevoStock = producto.getStockActual() - cantidad;
        producto.setStockActual(nuevoStock);
        
        // Si el stock llega a 0, marcar como agotado
        if (nuevoStock == 0) {
            producto.setEstadoProducto("AGOTADO");
        }
        
        return this.repository.save(producto);
    }

    public void delete(Long id) {
        Producto producto = this.findById(id);
        this.repository.delete(producto);
    }

    private void validateForCreate(Producto producto) {
        if (producto.getIdCategoria() == null) {
            throw new IllegalArgumentException("La categoría del producto es obligatoria");
        }
    }

    private void validateStateChange(String currentState, String newState) {
        if (currentState.equals(newState)) {
            return;
        }
        
        // Validaciones de transiciones de estado
        switch (currentState) {
            case "ACTIVO":
                if (!newState.equals("INACTIVO") && !newState.equals("AGOTADO")) {
                    throw new InvalidStateException(currentState, newState, "Producto");
                }
                break;
            case "INACTIVO":
                if (!newState.equals("ACTIVO")) {
                    throw new InvalidStateException(currentState, newState, "Producto");
                }
                break;
            case "AGOTADO":
                if (!newState.equals("ACTIVO") && !newState.equals("INACTIVO")) {
                    throw new InvalidStateException(currentState, newState, "Producto");
                }
                break;
            default:
                throw new InvalidStateException(currentState, newState, "Producto");
        }
    }
} 