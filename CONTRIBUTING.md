# Bidrag til Projekt

For at køre vores program, følg venligst nedenstående instruktioner:

## Forudsætninger

Før du begynder, sørg for at have installeret alle nødvendige teknologier for at køre programmet. Se listen over teknologier længere nede.

## Installation og Kørsel

1. **Klon repository:**
   - Besøg vores GitHub Repository på [wzNaji/ProjectManagement: initial (github.com)](https://github.com/wzNaji/ProjectManagement).

2. **Importer repository i din IDE:**
   - Brug Version Control til at importere projektet i din foretrukne IDE.

3. **Installer Dependencies:**
   - Kør følgende kommando for at installere nødvendige dependencies til projektet:
     ```
     mvn install
     ```

4. **Opsætning af Database:**
   - Importer SQL-filen i din foretrukne database-server applikation.
   - Opret databasen ved hjælp af de medfølgende scripts.

5. **Kør Projektet:**
   - Start projektet på den angivne port ved hjælp af:
     ```
     mvn spring-boot:run
     ```

6. **Åbn Applikationen:**
   - Åbn en webbrowser og naviger til `http://localhost:[PORT]` for at tilgå applikationen.

## Nødvendige Teknologier

Her er en liste over teknologier, der er nødvendige for at bruge projektets kalkulationsværktøj:

- **Java:** Anvendes til at udvikle backend af applikationen og have en API, der snakker med frontend.
- **Maven:** Bygger projektet og sørger for at indhente nødvendige dependencies.
- **MySQL:** Anvendes som en relationel database til at gemme og administrere data.
- **Microsoft Azure:** Applikationen hostes som en webservice.
- **Thymeleaf:** Java template engine, der letter dataudveksling mellem backend og frontend.
- **Spring Boot:** Frameworket der bruges til at hjælpe med udvikling af Java-applikationen.
- **HTML & CSS:** HTML strukturerer websiden, mens CSS anvendes til styling og design af websiden.

## Bidrag

Alle bidrag til projektet er velkomne. For detaljer om hvordan du kan bidrage, se vores guidelines for bidrag.
