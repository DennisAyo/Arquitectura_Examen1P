package com.banquito.sistema.originacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.banquito.sistema.originacion.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEstadoProducto(String estadoProducto);
    
    List<Producto> findByIdCategoria(Long idCategoria);
    
    List<Producto> findByNombreProductoContainingIgnoreCase(String nombreProducto);
    
    List<Producto> findByEstadoProductoAndIdCategoria(String estadoProducto, Long idCategoria);
    
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= :stockMinimo")
    List<Producto> findByStockBajo(@Param("stockMinimo") Integer stockMinimo);
    
    @Query("SELECT p FROM Producto p WHERE p.stockActual = 0")
    List<Producto> findProductosAgotados();
} 