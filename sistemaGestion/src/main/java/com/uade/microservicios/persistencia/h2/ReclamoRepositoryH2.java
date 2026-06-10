package com.uade.microservicios.persistencia.h2;

import com.uade.microservicios.dominio.reclamos.Reclamo;
import com.uade.microservicios.persistencia.IReclamoRepository;
import java.util.ArrayList;
import java.util.List;

public class ReclamoRepositoryH2 implements IReclamoRepository {
    private List<Reclamo> store = new ArrayList<>();

    @Override
    public void save(Reclamo r) { store.add(r); }

    @Override
    public Reclamo findById(String id) { return store.stream().filter(x -> true).findFirst().orElse(null); }

    @Override
    public List<Reclamo> findAll() { return new ArrayList<>(store); }
}

