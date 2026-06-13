package com.barrio.infraestructura.persistencia;

import java.util.List;
import java.util.Optional;

public interface Repositorio<T> {

    void guardar(T entidad);

    Optional<T> buscarPorId(Long id);

    List<T> buscarTodos();

    void eliminar(Long id);
}
