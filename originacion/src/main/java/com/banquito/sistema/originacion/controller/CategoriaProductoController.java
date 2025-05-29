package com.banquito.sistema.originacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banquito.sistema.originacion.model.CategoriaProducto;
import com.banquito.sistema.originacion.service.CategoriaProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias-producto")
@CrossOrigin(origins = "*")
public class CategoriaProductoController {

    private final CategoriaProductoService service;

    public CategoriaProductoController(CategoriaProductoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProducto>> findAll(
            @RequestParam(required = false) String nombre) {
        try {
            List<CategoriaProducto> categorias;
            if (nombre != null && !nombre.trim().isEmpty()) {
                categorias = this.service.findByNombreContaining(nombre);
            } else {
                categorias = this.service.findAll();
            }
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProducto> findById(@PathVariable Long id) {
        try {
            CategoriaProducto categoria = this.service.findById(id);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<CategoriaProducto> findByNombre(@PathVariable String nombre) {
        try {
            CategoriaProducto categoria = this.service.findByNombre(nombre);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CategoriaProducto> create(@Valid @RequestBody CategoriaProducto categoriaProducto) {
        try {
            CategoriaProducto savedCategoria = this.service.create(categoriaProducto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoria);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProducto> update(
            @PathVariable Long id, 
            @Valid @RequestBody CategoriaProducto categoriaProducto) {
        try {
            CategoriaProducto updatedCategoria = this.service.update(id, categoriaProducto);
            return ResponseEntity.ok(updatedCategoria);
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