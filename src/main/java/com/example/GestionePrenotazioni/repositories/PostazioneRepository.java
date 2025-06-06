package com.example.GestionePrenotazioni.repositories;

import com.example.GestionePrenotazioni.entities.Postazione;
import com.example.GestionePrenotazioni.enumerations.TipoPostazione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostazioneRepository extends JpaRepository<Postazione, Long> {
    List<Postazione> findByTipoAndEdificio_Citta(TipoPostazione tipo, String citta);
}