package com.example.GestionePrenotazioni.repositories;

import com.example.GestionePrenotazioni.entities.Prenotazione;
import com.example.GestionePrenotazioni.entities.Postazione;
import com.example.GestionePrenotazioni.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    boolean existsByPostazioneAndData(Postazione postazione, LocalDate data);
    boolean existsByUtenteAndData(Utente utente, LocalDate data);}