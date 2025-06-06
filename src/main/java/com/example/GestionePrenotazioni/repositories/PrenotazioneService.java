package com.example.GestionePrenotazioni.repositories;

import com.example.GestionePrenotazioni.entities.*;
import com.example.GestionePrenotazioni.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final PostazioneRepository postazioneRepository;
    private final UtenteRepository utenteRepository;

    public List<Postazione> ricercaPostazioni(String citta, String tipo) {
        return postazioneRepository.findByTipoAndEdificio_Citta(
                Enum.valueOf(com.example.GestionePrenotazioni.enumerations.TipoPostazione.class, tipo.toUpperCase()),
                citta
        );
    }

    @Transactional
    public String prenotaPostazione(Long utenteId, Long postazioneId, LocalDate data) {
        Utente utente = utenteRepository.findById(utenteId).orElseThrow();
        Postazione postazione = postazioneRepository.findById(postazioneId).orElseThrow();

        if (prenotazioneRepository.existsByUtenteAndData(utente, data)) {
            return "Errore: L'utente ha già una prenotazione per questa data.";
        }

        if (prenotazioneRepository.existsByPostazioneAndData(postazione, data)) {
            return "Errore: La postazione è già prenotata per questa data.";
        }

        Prenotazione prenotazione = Prenotazione.builder()
                .utente(utente)
                .postazione(postazione)
                .data(data)
                .build();

        prenotazioneRepository.save(prenotazione);
        return "Prenotazione avvenuta con successo.";
    }
}
