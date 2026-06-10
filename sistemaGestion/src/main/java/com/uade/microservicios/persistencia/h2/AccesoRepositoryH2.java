package com.uade.microservicios.persistencia.h2;

import com.uade.microservicios.dominio.accesos.RegistroAcceso;
import com.uade.microservicios.persistencia.IAccesoRepository;
import java.util.ArrayList;
import java.util.List;

public class AccesoRepositoryH2 implements IAccesoRepository {
    private List<RegistroAcceso> store = new ArrayList<>();

    @Override
    public void save(RegistroAcceso r) { store.add(r); }

    @Override
    public List<RegistroAcceso> findAll() { return new ArrayList<>(store); }
}

