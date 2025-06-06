package com.example.GestionePrenotazioni;

import com.example.GestionePrenotazioni.entities.Edificio;
import com.example.GestionePrenotazioni.entities.Postazione;
import com.example.GestionePrenotazioni.entities.Prenotazione;
import com.example.GestionePrenotazioni.entities.Utente;
import com.example.GestionePrenotazioni.enumerations.TipoPostazione;
import com.example.GestionePrenotazioni.repositories.EdificioRepository;
import com.example.GestionePrenotazioni.repositories.PostazioneRepository;
import com.example.GestionePrenotazioni.repositories.PrenotazioneRepository;
import com.example.GestionePrenotazioni.repositories.UtenteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class AppRunner implements CommandLineRunner {

    private final EdificioRepository edificioRepo;
    private final PostazioneRepository postazioneRepo;
    private final UtenteRepository utenteRepo;
    private final PrenotazioneRepository prenotazioneRepo;

    public AppRunner(EdificioRepository edificioRepo,
                     PostazioneRepository postazioneRepo,
                     UtenteRepository utenteRepo,
                     PrenotazioneRepository prenotazioneRepo) {
        this.edificioRepo = edificioRepo;
        this.postazioneRepo = postazioneRepo;
        this.utenteRepo = utenteRepo;
        this.prenotazioneRepo = prenotazioneRepo;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("======== GESTIONE PRENOTAZIONI ========");

        while (running) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Inserisci nuovo utente");
            System.out.println("2. Inserisci nuovo edificio");
            System.out.println("3. Inserisci nuova postazione");
            System.out.println("4. Effettua una prenotazione");
            System.out.println("5. Esci");
            System.out.print("Seleziona un'opzione: ");

            String scelta = scanner.nextLine();

            try {
                switch (scelta) {
                    case "1" -> inserisciUtente(scanner);
                    case "2" -> inserisciEdificio(scanner);
                    case "3" -> inserisciPostazione(scanner);
                    case "4" -> effettuaPrenotazione(scanner);
                    case "5" -> running = false;
                    default -> System.out.println("❌ Scelta non valida. Riprova.");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Errore: " + e.getMessage());
            }
        }

        System.out.println("👋 Uscita dall'applicazione. Arrivederci!");
        scanner.close();
    }

    private void inserisciUtente(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Utente utente = new Utente(null, username, nome, email);
        utenteRepo.save(utente);
        System.out.println("✅ Utente salvato con successo.");
    }

    private void inserisciEdificio(Scanner scanner) {
        System.out.print("Nome edificio: ");
        String nome = scanner.nextLine();
        System.out.print("Indirizzo: ");
        String indirizzo = scanner.nextLine();
        System.out.print("Città: ");
        String citta = scanner.nextLine();

        Edificio edificio = new Edificio(null, nome, indirizzo, citta);
        edificioRepo.save(edificio);
        System.out.println("✅ Edificio salvato con successo.");
    }

    private void inserisciPostazione(Scanner scanner) {
        System.out.print("Codice postazione: ");
        String codice = scanner.nextLine();
        System.out.print("Descrizione: ");
        String descrizione = scanner.nextLine();
        System.out.print("Tipo (PRIVATO, OPENSPACE, SALA_RIUNIONI): ");
        TipoPostazione tipo = TipoPostazione.valueOf(scanner.nextLine().toUpperCase());
        System.out.print("Max occupanti: ");
        int max = Integer.parseInt(scanner.nextLine());

        List<Edificio> edifici = edificioRepo.findAll();
        if (edifici.isEmpty()) {
            System.out.println("⚠️ Nessun edificio disponibile. Inserisci prima un edificio.");
            return;
        }

        System.out.println("Scegli un edificio (ID): ");
        edifici.forEach(e -> System.out.printf("%d: %s (%s)\n", e.getId(), e.getNome(), e.getCitta()));
        long idEdificio = Long.parseLong(scanner.nextLine());
        Optional<Edificio> edificioOpt = edificioRepo.findById(idEdificio);

        if (edificioOpt.isEmpty()) {
            System.out.println("❌ Edificio non trovato.");
            return;
        }

        Postazione postazione = Postazione.builder()
                .codice(codice)
                .descrizione(descrizione)
                .tipo(tipo)
                .maxOccupanti(max)
                .edificio(edificioOpt.get())
                .build();

        postazioneRepo.save(postazione);
        System.out.println("✅ Postazione salvata con successo.");
    }

    private void effettuaPrenotazione(Scanner scanner) {
        List<Utente> utenti = utenteRepo.findAll();
        if (utenti.isEmpty()) {
            System.out.println("⚠️ Nessun utente disponibile.");
            return;
        }

        System.out.println("Scegli un utente (username): ");
        utenti.forEach(u -> System.out.printf("- %s (%s)\n", u.getUsername(), u.getNomeCompleto()));
        String username = scanner.nextLine().trim();
        Optional<Utente> utenteOpt = utenteRepo.findByUsername(username);
        if (utenteOpt.isEmpty()) {
            System.out.println("❌ Utente non trovato.");
            return;
        }
        Utente utente = utenteOpt.get();

        System.out.print("Inserisci data prenotazione (YYYY-MM-DD): ");
        LocalDate data = LocalDate.parse(scanner.nextLine());

        System.out.print("Tipo postazione desiderata (PRIVATO, OPENSPACE, SALA_RIUNIONI): ");
        TipoPostazione tipo = TipoPostazione.valueOf(scanner.nextLine().toUpperCase());

        System.out.print("Città: ");
        String citta = scanner.nextLine();

        List<Postazione> disponibili = postazioneRepo.findByTipoAndEdificio_Citta(tipo, citta);

        if (disponibili.isEmpty()) {
            System.out.println("❌ Nessuna postazione disponibile per la ricerca.");
            return;
        }

        System.out.println("Postazioni disponibili:");
        disponibili.forEach(p -> System.out.printf("%d: %s (%s)\n", p.getId(), p.getDescrizione(), p.getCodice()));

        System.out.print("Scegli ID postazione: ");
        long idPostazione = Long.parseLong(scanner.nextLine());
        Optional<Postazione> postazioneOpt = postazioneRepo.findById(idPostazione);

        if (postazioneOpt.isEmpty()) {
            System.out.println("❌ Postazione non trovata.");
            return;
        }

        Postazione postazione = postazioneOpt.get();

        boolean giàPrenotato = prenotazioneRepo.existsByUtenteAndData(utente, data);
        boolean occupata = prenotazioneRepo.existsByPostazioneAndData(postazione, data);

        if (giàPrenotato) {
            System.out.println("❌ L'utente ha già una prenotazione per questa data.");
        } else if (occupata) {
            System.out.println("❌ Postazione già prenotata per questa data.");
        } else {
            Prenotazione prenotazione = new Prenotazione(null, utente, postazione, data);
            prenotazioneRepo.save(prenotazione);
            System.out.println("✅ Prenotazione salvata con successo.");
        }
    }
}