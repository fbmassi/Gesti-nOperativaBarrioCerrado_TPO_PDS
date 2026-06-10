package com.uade.microservicios.persistencia.h2;

import com.uade.microservicios.dominio.mantenimiento.TareaMantenimiento;
import com.uade.microservicios.persistencia.ITareaRepository;
import java.util.ArrayList;
import java.util.List;

public class TareaRepositoryH2 implements ITareaRepository {
    private List<TareaMantenimiento> store = new ArrayList<>();

    @Override
    public void save(TareaMantenimiento t) { store.add(t); }

    @Override
    public TareaMantenimiento findById(String id) { return store.stream().filter(x -> true).findFirst().orElse(null); }

    @Override
    public List<TareaMantenimiento> findAll() { return new ArrayList<>(store); }
}

