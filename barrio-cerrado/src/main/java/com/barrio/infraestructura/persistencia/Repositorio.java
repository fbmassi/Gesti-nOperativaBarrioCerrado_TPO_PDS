package com.barrio.infraestructura.persistencia;

import java.util.List;

public interface Repositorio<T> {

    void guardar(T entidad);

    T buscarPorId(Long id);

    List<T> buscarTodos();

    void eliminar(Long id);
}
