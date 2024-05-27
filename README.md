# Projektledelse

Velkommen til GitHub-repositoriet for ProjectManagement. Dette projekt er designet til at forbedre og optimere projektstyringsprocesser ved at tilvejebringe en integreret platform for estimering, ressourceallokering, tidsplanlægning, risikovurdering og opfølgning på performance. Vores mål er at simplificere og forbedre projektlederes arbejde med at planlægge, og overvåge projekter for at forbedre beslutningstagning og effektiv ressourceudnyttelse.

## Teknologi Stack
- **Backend**: Anvender Spring Boot for server-side logik.
- **Frontend**: Implementeret med Thymeleaf for templating og understøttet af CSS og Bootstrap for styling.
- **Database**: Bruger MySQL til datalagring.
- **CI/CD**: Integrerer GitHub Actions for automatiseret testing og deployment.
- **Deployment**: Deployes på Microsoft Azure via Azure Web Apps.

## Funktionaliteter

### Brugerhåndtering
- **Log ind og Registrer**: Brugere kan oprette en ny konto eller logge ind på en eksisterende.
- **Vis Brugere**: Tilgængelig for alle, viser en liste over registrerede brugere med roller og kontaktoplysninger.
- **Administrer Brugere**: Kun tilgængelig for 'ADMIN' og 'MANAGER' roller til redigering og sletning af brugere.

### Projektstyring
- **Håndtering af Projekter og Subprojekter**: Tillader oprettelse, redigering og sletning af projekter, kun tilgængelig for 'ADMIN' og 'MANAGER'.
- **Tilknytning af Brugere**: Administrer hvilke brugere der er tilknyttet hvilke projekter.
- **Oversigt over Projektmedlemmer**: Viser hvem der arbejder på et projekt med mulighed for kontakt.

### Opgavestyring
- **Opret Task**: Alle projektmedlemmer kan oprette opgaver indenfor deres tildelte projekter.
- **Administrer Tasks**: Kun 'ADMIN' og 'MANAGER' kan redigere og slette opgaver.

## Kom i Gang

1. **Klon repositoriet**: [GitHub Link](https://github.com/wzNaji/ProjectManagement.git)
2. **Konfigurer MySQL-databasen** ved at oprette den og køre de nødvendige scripts.
3. **Sæt databaseforbindelser op** i `src/main/resources/application-dev.properties`.
4. **Byg og kør projektet med Maven**.
5. **Applikationen er nu tilgængelig** på din lokale server.

## Testing og Deployment
Projektet benytter GitHub Actions til at håndtere CI/CD-processer, som sikrer kontinuerlig integration og deployment til Azure.

## Bidrag
Alle bidrag er velkomne. For retningslinjer, se vores CONTRIBUTING.md.

## Kontakt
Spørgsmål kan rettes via e-mail: wana0001@stud.kea.dk
