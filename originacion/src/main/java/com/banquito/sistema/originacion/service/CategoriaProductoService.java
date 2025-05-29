package com.banquito.sistema.originacion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.sistema.originacion.exception.DuplicateException;
import com.banquito.sistema.originacion.exception.NotFoundException;
import com.banquito.sistema.originacion.model.CategoriaProducto;
import com.banquito.sistema.originacion.repository.CategoriaProductoRepository;

@Service
@Transactional
public class CategoriaProductoService {

    private final CategoriaProductoRepository repository;

    public CategoriaProductoService(CategoriaProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaProducto> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public CategoriaProducto findById(Long id) {
        Optional<CategoriaProducto> categoria = this.repository.findById(id);
        if (categoria.isEmpty()) {
            throw new NotFoundException(id.toString(), "CategoriaProducto");
        }
        return categoria.get();
    }

    @Transactional(readOnly = true)
    public CategoriaProducto findByNombre(String nombreCategoria) {
        Optional<CategoriaProducto> categoria = this.repository.findByNombreCategoria(nombreCategoria);
        if (categoria.isEmpty()) {
            throw new NotFoundException(nombreCategoria, "CategoriaProducto");
        }
        return categoria.get();
    }

    @Transactional(readOnly = true)
    public List<CategoriaProducto> findByNombreContaining(String nombreCategoria) {
        return this.repository.findByNombreCategoriaContainingIgnoreCase(nombreCategoria);
    }

    public CategoriaProducto create(CategoriaProducto categoriaProducto) {
        this.validateForCreate(categoriaProducto);
        return this.repository.save(categoriaProducto);
    }

    public CategoriaProducto update(Long id, CategoriaProducto categoriaProducto) {
        CategoriaProducto existingCategoria = this.findById(id);
        this.validateForUpdate(categoriaProducto, existingCategoria);
        
        categoriaProducto.setIdCategoria(id);
        categoriaProducto.setVersion(existingCategoria.getVersion());
        
        return this.repository.save(categoriaProducto);
    }

    public void delete(Long id) {
        CategoriaProducto categoria = this.findById(id);
        this.repository.delete(categoria);
    }

    private void validateForCreate(CategoriaProducto categoriaProducto) {
        if (this.repository.existsByNombreCategoria(categoriaProducto.getNombreCategoria())) {
            throw new DuplicateException(categoriaProducto.getNombreCategoria(), "CategoriaProducto con nombre");
        }
    }

    private void validateForUpdate(CategoriaProducto categoriaProducto, CategoriaProducto existing) {
        if (!categoriaProducto.getNombreCategoria().equals(existing.getNombreCategoria()) && 
            this.repository.existsByNombreCategoria(categoriaProducto.getNombreCategoria())) {
            throw new DuplicateException(categoriaProducto.getNombreCategoria(), "CategoriaProducto con nombre");
        }
    }
} 