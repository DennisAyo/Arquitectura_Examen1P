package com.banquito.sistema.originacion.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banquito.sistema.originacion.model.Producto;
import com.banquito.sistema.originacion.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> findAll(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer stockMinimo) {
        try {
            List<Producto> productos;
            
            if (stockMinimo != null) {
                productos = this.service.findByStockBajo(stockMinimo);
            } else if (estado != null && categoriaId != null) {
                productos = this.service.findByEstadoAndCategoria(estado, categoriaId);
            } else if (estado != null) {
                productos = this.service.findByEstado(estado);
            } else if (categoriaId != null) {
                productos = this.service.findByCategoria(categoriaId);
            } else if (nombre != null && !nombre.trim().isEmpty()) {
                productos = this.service.findByNombre(nombre);
            } else {
                productos = this.service.findAll();
            }
            
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> findById(@PathVariable Long id) {
        try {
            Producto producto = this.service.findById(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/agotados")
    public ResponseEntity<List<Producto>> findProductosAgotados() {
        try {
            List<Producto> productos = this.service.findProductosAgotados();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Producto> create(@Valid @RequestBody Producto producto) {
        try {
            Producto savedProducto = this.service.create(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Producto> changeState(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String newState = request.get("estado");
            String motivo = request.get("motivo");
            
            if (newState == null || newState.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Producto updatedProducto = this.service.changeState(id, newState, motivo);
            return ResponseEntity.ok(updatedProducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/aumentar-stock")
    public ResponseEntity<Producto> aumentarStock(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Integer cantidad = (Integer) request.get("cantidad");
            BigDecimal precioCompra = new BigDecimal(request.get("precioCompra").toString());
            
            if (cantidad == null || precioCompra == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Producto updatedProducto = this.service.aumentarStock(id, cantidad, precioCompra);
            return ResponseEntity.ok(updatedProducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/disminuir-stock")
    public ResponseEntity<Producto> disminuirStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer cantidad = request.get("cantidad");
            
            if (cantidad == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Producto updatedProducto = this.service.disminuirStock(id, cantidad);
            return ResponseEntity.ok(updatedProducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            this.service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 