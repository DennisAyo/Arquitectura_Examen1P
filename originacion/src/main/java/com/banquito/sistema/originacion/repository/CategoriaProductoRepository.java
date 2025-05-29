package com.banquito.sistema.originacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.sistema.originacion.model.CategoriaProducto;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {

    Optional<CategoriaProducto> findByNombreCategoria(String nombreCategoria);
    
    List<CategoriaProducto> findByNombreCategoriaContainingIgnoreCase(String nombreCategoria);
    
    boolean existsByNombreCategoria(String nombreCategoria);
} 