package com.Floreria.servicio;

import com.Floreria.modelo.Flor;
import com.Floreria.repositorio.FlorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FlorService {
    @Autowired private FlorRepository repo;
    public List<Flor> listar() { return repo.findAll(); }
    public void guardar(Flor f) { repo.save(f); }
    public Flor buscar(Long id) { return repo.findById(id).orElse(null); }
    public void eliminar(Long id) { repo.deleteById(id); }
}