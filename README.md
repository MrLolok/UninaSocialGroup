# Avvio dell'applicazione
## Prerequisiti
- È necessario disporre di una base di dati conforme ai dati e alle operazioni effettuabili sulle entità definite all'interno del codice di UninaSocialGroup. Per semplicità, è stato disposto il file docker-compose.yml per avviare un'immagine di PostgreSQL che fornirà questa base di dati già configurata e pronta all'uso.
## Avvio
1. Eseguire il comando `mvn clean package` per compilare il codice e creare il rispettivo archivio JAR.
2. Doppio click sul JAR generato o eseguire il comando `java -jar UninaSocialGroup*.jar -Xmx2048M` per avviare l'interfaccia.
	**Nota:** se non viene mostrata alcuna interfaccia, probabilmente ciò è causato dal fatto che non sia stato configurato correttamente il file `database.json` situato nella cartella `configs`  