ProjectManagement

Velkommen til GitHub repositoriet for ProjectManagement.
Applikationen har til formål at optimere projektstyringsprocesser ved at tilbyde værktøjer til estimering, ressourceallokering, tidsplanlægning,
risikovurdering og performance tracking. Ved at integrere disse funktioner i én platform, sigter vi mod at forenkle og forbedre måden, hvorpå projektledere planlægger,
overvåger og rapporterer om projekter, hvilket ultimativt vil føre til bedre beslutningstagning og ressourceudnyttelse.

Dette projekt benytter Spring Boot for backend logik, Thymeleaf for frontend templating, og MySQL for databasestyring.
Projektet er designet til at blive deployed på Microsoft Azure ved brug af GitHub Actions for automatiseret testing og deployment.

Funktioner

Log ind og Registrer:
Brugere har mulighed for at logge ind på en eksisterende konto, eller registrere sig som en ny bruger.

Vis brugere:
Denne funktion giver mulighed for at se alle registrerede brugere. Dette funktion danner overblik over medlemmere i virksomheden,
samt roller og kontaktoplyninger, så man nemt kan søge hjælp eller lign. ved at kontakte pågældende bruger.

Slet, rediger brugere:
Kun brugere med 'ADMIN' og 'MANAGER' roller kan benytte sig af denne funktion af sikkerhedsmæssige årsager.

Opret, slet, rediger projekter, subprojekter:
Disse funktioner giver mulighed for at skabe overblik over et projekt. Projektets information indeholder data såsom, tidestimering, budget, start- og slut dato.
Kun brugere med 'ADMIN' og 'MANAGER' roller kan benytte sig af denne funktion af sikkerhedsmæssige årsager.

Tilføj og slet brugere til/fra projekt:
Denne funktion bruges til at tilføje brugere til specifikke projekter, og Kun brugere med 'ADMIN' og 'MANAGER' roller kan benytte sig af denne funktion.

Vis brugere tilknyttet til projekt:
Denne funktion vises for alle der er tilknyttet projektet, samt 'ADMIN' og 'MANAGER' brugere. Dette kan skabe en bedre overblik over hvem der er arbejder på projektet, 
og viser samtidigt kontaktoplysninger på projekt-medlemmere, så en udvikler/arbejder nemt kan finde frem til hvem og hvor man kan søge hjælp til projektet.

Opret task:
Denne funktion er tilknyttet et subprojekt, og alle medlemmere, der er tilføjet til en projekt, kan tilgå denne funktion.
Det giver mulighed for udviklere/arbejdere selv kan oprette små opgaver, på egen initiativ.

Slet og redigering af tasks:
Kun brugere med 'ADMIN' og 'MANAGER' roller kan benytte sig af denne funktion, da tasks skal godkendes af projektlederen for at kunne afsluttes.


Teknologier

Backend: Spring Boot (Java)
Frontend: Thymeleaf, CSS, Bootstrap
Database: MySQL
CI/CD: GitHub ActionsDeployment: Azure Web Apps


Get started

1. Klon repositoriet:
   https://github.com/wzNaji/ProjectManagement.git

2. Konfigurer MySQL database: Opret en database f.eks. 'ProjectManagement' og kør SQL scripts fra /docs/sql_script/DDL for at oprette tabeller.

3. Konfigurer applikationsindstillinger: Rediger src/main/resources/application-dev.properties for at indstille database tilslutninger:
spring.datasource.url=jdbc:mysql://localhost:3306/ProjectManagement?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password

Set derefter application.properties: spring.profiles.active=dev

4. Byg og kør projektet med maven

5. Applikationen kan nu findes på localhost. f.eks. localhost:8080
   

Testing og deployment
CI/CD

Dette projekt bruger GitHub Actions for kontinuerlig integration og deployment.
Workflowet for CI/CD processerne er defineret under /.github/workflows , som inkluderer opgaver som automatiske tests, bygninger, og deployment til Azure Web Apps.


Bidrag
Vi byder bidrag velkomne fra alle! Venligst læs CONTRIBUTE.md for retningslinjerne om, hvordan du kan indsende bugs, forslag til forbedringer, og pull requests.

Kontakt
Hvis du har spørgsmål, er du velkommen til at sende en email til wana0001@stud.kea.dk
