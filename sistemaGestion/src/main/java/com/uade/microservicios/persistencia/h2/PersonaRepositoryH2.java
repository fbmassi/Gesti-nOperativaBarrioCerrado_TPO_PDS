package com.uade.microservicios.persistencia.h2;

import com.uade.microservicios.dominio.personas.Persona;
import com.uade.microservicios.persistencia.IPersonaRepository;
import java.util.ArrayList;
import java.util.List;

public class PersonaRepositoryH2 implements IPersonaRepository {
    private List<Persona> store = new ArrayList<>();

    @Override
    public void save(Persona p) { store.add(p); }

    @Override
    public Persona findById(String id) { return store.stream().filter(x -> true).findFirst().orElse(null); }

    @Override
    public List<Persona> findAll() { return new ArrayList<>(store); }
}

