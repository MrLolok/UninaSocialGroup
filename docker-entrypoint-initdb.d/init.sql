-- Definizione del datatype GENDER
CREATE TYPE GENDER AS ENUM ('male', 'female', 'other');
-- Definizione del datatype T_GROUP per classificare i gruppi
CREATE TYPE T_GROUP AS ENUM ('open', 'invite', 'closed');
-- Definizione del datatype T_POST per classificare i post
CREATE TYPE T_POST AS ENUM ('text', 'photo');

CREATE TABLE IF NOT EXISTS utente
(
    id_utente     SERIAL PRIMARY KEY,
    nome          VARCHAR(50)   NOT NULL,
    cognome       VARCHAR(50)   NOT NULL,
    email         VARCHAR(255)  NOT NULL,
    password      VARCHAR(1000) NOT NULL,
    eta           SMALLINT      NOT NULL,
    biografia     VARCHAR(2000),
    avatar        VARCHAR(1000),
    genere        GENDER,
    registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gruppo
(
    id_gruppo   SERIAL PRIMARY KEY,
    fk_creatore INTEGER     NOT NULL REFERENCES utente,
    nome        VARCHAR(50) NOT NULL,
    descrizione VARCHAR(2000),
    tipo        T_GROUP     NOT NULL,
    creazione   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS membro
(
    fk_utente INTEGER NOT NULL REFERENCES utente,
    fk_gruppo INTEGER NOT NULL REFERENCES gruppo
);

CREATE TABLE IF NOT EXISTS richiestaaccesso
(
    id_richiesta SERIAL PRIMARY KEY,
    fk_utente    INTEGER NOT NULL REFERENCES utente,
    fk_gruppo    INTEGER NOT NULL REFERENCES gruppo,
    data         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tag
(
    keyword VARCHAR(50) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS classificazione
(
    fk_tag    VARCHAR(50) NOT NULL REFERENCES tag,
    fk_gruppo INTEGER     NOT NULL REFERENCES gruppo
);

CREATE TABLE IF NOT EXISTS post
(
    id_post    SERIAL PRIMARY KEY,
    fk_utente  INTEGER NOT NULL REFERENCES utente,
    fk_gruppo  INTEGER NOT NULL REFERENCES gruppo,
    tipo       T_POST  NOT NULL,
    contenuto  VARCHAR(2000),
    silenzioso BOOLEAN,
    ultima_mod TIMESTAMP,
    data       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifica
(
    fk_post   INTEGER NOT NULL REFERENCES post,
    fk_utente INTEGER NOT NULL REFERENCES utente
);

CREATE TABLE IF NOT EXISTS commento
(
    id_commento      SERIAL PRIMARY KEY,
    fk_utente        INTEGER NOT NULL REFERENCES utente,
    fk_post          INTEGER NOT NULL REFERENCES post,
    fk_commentopadre INTEGER REFERENCES commento,
    contenuto        VARCHAR(2000),
    data             TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS likepost
(
    id_likepost SERIAL PRIMARY KEY,
    fk_utente   INTEGER NOT NULL REFERENCES utente,
    fk_post     INTEGER NOT NULL REFERENCES post,
    data        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS likecommento
(
    id_likecommento SERIAL PRIMARY KEY,
    fk_utente       INTEGER NOT NULL REFERENCES utente,
    fk_commento     INTEGER NOT NULL REFERENCES commento,
    data            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--
-- CONTROLLI SUI DATI DEGLI UTENTI
--
CREATE OR REPLACE FUNCTION CheckUserData()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 16). Controlla il formato dell'email
    -- Nota: La E prima della RegEx permette l'interpretazione corretta dei
    -- caratteri di escape all'interno della stringa.
    IF NOT NEW.email ~* E'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$' THEN
        RAISE EXCEPTION USING HINT = 'Formato email non valido: ' || NEW.email, ERRCODE = 'invalid_parameter_value';
    END IF;

    -- VINCOLO (ID 1). Controlla unicità dell'email
    IF EXISTS (SELECT 1 FROM Utente WHERE email = NEW.email AND ID_Utente <> NEW.ID_Utente) THEN
        RAISE EXCEPTION USING HINT = 'Utente con email duplicata: ' || NEW.email, ERRCODE = 'unique_violation';
    END IF;

    -- VINCOLO (ID 17). Controlla sicurezza della password
    IF NOT (NEW.password ~ '^(?=.*\d)(?=.*[a-zA-Z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?:{}|<>]).{8,}$') THEN
        RAISE EXCEPTION USING HINT =
                'La password deve contenere almeno 8 caratteri, un numero, un carattere speciale, e una lettera maiuscola', ERRCODE = 'invalid_parameter_value';
    END IF;

    -- VINCOLO (ID 15). Controlla URL dell'avatar se presente
    IF NEW.avatar IS NOT NULL AND NOT (NEW.avatar ~ '^(http://|https://)') THEN
        RAISE EXCEPTION USING HINT = 'URL avatar deve iniziare con "http://" o "https://"', ERRCODE = 'invalid_parameter_value';
    END IF;

    -- Vincolo (ID 29). La data di registrazione deve essere immutabile
    IF TG_OP = 'UPDATE' THEN
        NEW.registrazione := OLD.registrazione;
    -- Vincolo (ID 8). Controlla che data e ora di registrazione non siano oltre la data e ora corrente
    ELSIF TG_OP = 'INSERT' THEN
        IF NEW.registrazione > CURRENT_TIMESTAMP THEN
            RAISE EXCEPTION USING HINT = 'Data di registrazione non valida: ' || NEW.registrazione, ERRCODE = 'invalid_parameter_value';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER UserDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "utente"
    FOR EACH ROW
EXECUTE FUNCTION CheckUserData();

--
-- GESTIONE DELL'ELIMINAZIONE DI UN UTENTE
--
CREATE OR REPLACE FUNCTION HandleUserDeletion()
    RETURNS TRIGGER AS
$$
DECLARE
    id utente.ID_Utente%TYPE := OLD.ID_Utente;
BEGIN
    -- VINCOLO (ID 22). Elimina dati ed entità associate all'utente
    DELETE FROM "gruppo" WHERE FK_Creatore = id;
    DELETE FROM "notifica" WHERE FK_Utente = id;
    DELETE FROM "post" WHERE FK_Utente = id;
    DELETE FROM "membro" WHERE FK_Utente = id;
    DELETE FROM "richiestaaccesso" WHERE FK_Utente = id;
    DELETE FROM "likepost" WHERE FK_Utente = id;
    DELETE FROM "likecommento" WHERE FK_Utente = id;
    DELETE FROM "commento" WHERE FK_Utente = id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER UserDelete
    BEFORE DELETE
    ON "utente"
    FOR EACH ROW
EXECUTE FUNCTION HandleUserDeletion();

--
-- CONTROLLI SUI DATI DEI GRUPPI
--
CREATE OR REPLACE FUNCTION CheckGroupData()
    RETURNS TRIGGER AS
$$
BEGIN
    IF TG_OP = 'UPDATE' OR TG_OP = 'INSERT' THEN
        -- VINCOLO (ID 2). Controlla unicità del nome
        IF EXISTS (SELECT 1 FROM Gruppo WHERE nome = NEW.nome AND ID_Gruppo <> NEW.ID_Gruppo) THEN
            RAISE EXCEPTION USING HINT = 'Gruppo con nome duplicato: ' || NEW.nome, ERRCODE = 'unique_violation';
        END IF;
    END IF;

    -- Vincolo (ID 30). La data di creazione deve essere immutabile
    IF TG_OP = 'UPDATE' THEN
        NEW.creazione := OLD.creazione;
    -- Vincolo (ID 9). Controlla che data e ora di creazione non siano oltre la data e ora corrente
    ELSIF TG_OP = 'INSERT' THEN
        IF NEW.creazione > CURRENT_TIMESTAMP THEN
            RAISE EXCEPTION USING HINT = 'Data di creazione non valida: ' || NEW.creazione, ERRCODE = 'invalid_parameter_value';
        END IF;
    END IF;

    -- VINCOLO (ID 5). Singolo creatore per ogni gruppo
    -- Non eseguiamo alcuna operazione poiché, ogni record di gruppo,
    -- possiede l'attributo FK_Creatore che non può essere NULL ed ha
    -- molteplicità [1]. Questo implica che un gruppo automaticamente
    -- ha un singolo creatore.

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER GroupDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "gruppo"
    FOR EACH ROW
EXECUTE FUNCTION CheckGroupData();

--
-- GESTIONE DELLE RICHIESTE DI ACCESSO SEGUITE DALLA MODIFICA DI UN GRUPPO
--
CREATE OR REPLACE FUNCTION HandleGroupTypeChange()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 36). Se il nuovo tipo del gruppo è OPEN,
    -- tutte le richieste di accesso al gruppo vengono automaticamente accettate
    IF NEW.tipo = 'open' THEN
        INSERT INTO Membro (FK_Utente, FK_Gruppo)
        SELECT FK_Utente, FK_Gruppo
        FROM RichiestaAccesso
        WHERE FK_Gruppo = NEW.ID_Gruppo;
    END IF;

    -- VINCOLO (ID 36/37). Se il nuovo tipo del gruppo è OPEN/CLOSED,
    -- tutte le richieste di accesso al gruppo vengono automaticamente eliminate
    IF NEW.tipo <> 'invite' THEN
        DELETE FROM RichiestaAccesso WHERE FK_Gruppo = NEW.ID_Gruppo;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER GroupTypeChange
    AFTER UPDATE OF "tipo"
    ON "gruppo"
    FOR EACH ROW
EXECUTE FUNCTION HandleGroupTypeChange();

--
-- GESTIONE DELL'ELIMINAZIONE DI UN GRUPPO
--
CREATE OR REPLACE FUNCTION HandleGroupDeletion()
    RETURNS TRIGGER AS
$$
DECLARE
    id gruppo.ID_Gruppo%TYPE := OLD.ID_Gruppo;
BEGIN
    -- VINCOLO (ID 23). Elimina dati ed entità associate al gruppo
    DELETE FROM Post WHERE FK_Gruppo = id;
    DELETE FROM Membro WHERE FK_Gruppo = id;
    DELETE FROM Classificazione WHERE FK_Gruppo = id;
    DELETE FROM RichiestaAccesso WHERE FK_Gruppo = id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER GroupDelete
    BEFORE DELETE
    ON "gruppo"
    FOR EACH ROW
EXECUTE FUNCTION HandleGroupDeletion();

--
-- CONTROLLI SUI DATI DELLE RICHIESTE
--
CREATE OR REPLACE FUNCTION CheckAccessRequestData()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 39). Controlla unicità della richiesta
    IF EXISTS (SELECT 1
               FROM RichiestaAccesso
               WHERE FK_Gruppo = NEW.FK_Gruppo
                 AND FK_Utente = NEW.FK_Utente
                 AND ID_Richiesta <> NEW.ID_Richiesta) THEN
        RAISE EXCEPTION USING HINT = 'Richiesta duplicata, utente: ' || NEW.FK_Utente, ERRCODE = 'unique_violation';
    END IF;
    -- VINCOLO (ID 20). Controllo tipo di gruppo a cui si sta facendo la richiesta
    IF EXISTS (SELECT 1 FROM Gruppo WHERE ID_Gruppo = NEW.FK_Gruppo AND tipo <> 'invite') THEN
        RAISE EXCEPTION USING HINT = 'Gruppo non di tipo invite', ERRCODE = 'invalid_column_reference';
    END IF;
    -- VINCOLO (ID 21). Controllo membri del gruppo a cui si sta facendo la richiesta
    IF EXISTS (SELECT 1 FROM Membro WHERE FK_Utente = NEW.FK_Utente AND FK_Gruppo = NEW.FK_Gruppo) THEN
        RAISE EXCEPTION USING HINT = 'Utente già membro del gruppo', ERRCODE = 'invalid_column_reference';
    END IF;

    -- Vincolo (ID 31). La data di richiesta deve essere immutabile
    IF TG_OP = 'UPDATE' THEN
        NEW.data := OLD.data;
        -- Vincolo (ID 10). Controlla che data e ora di richiesta non siano oltre la data e ora corrente
    ELSIF TG_OP = 'INSERT' THEN
        IF NEW.data > CURRENT_TIMESTAMP THEN
            RAISE EXCEPTION USING HINT = 'Data di richiesta non valida: ' || NEW.data, ERRCODE = 'invalid_parameter_value';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER AccessRequestDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "richiestaaccesso"
    FOR EACH ROW
EXECUTE FUNCTION CheckAccessRequestData();

--
-- CONTROLLI SUI DATI DEI MEMBRI
--
CREATE OR REPLACE FUNCTION CheckMemberData()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 6). Controlla unicità della richiesta
    IF EXISTS (SELECT 1 FROM Membro WHERE FK_Gruppo = NEW.FK_Gruppo AND FK_Utente = NEW.FK_Utente) THEN
        RAISE EXCEPTION USING HINT = 'Membro duplicato, esiste già.', ERRCODE = 'invalid_parameter_value';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER MemberDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "membro"
    FOR EACH ROW
EXECUTE FUNCTION CheckMemberData();

--
-- GESTIONE DELL'ELIMINAZIONE DI UN MEMBRO
--
CREATE OR REPLACE FUNCTION HandleMemberDeletion()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 40). Elimina dati ed entità associate al gruppo
    DELETE
    FROM Notifica
    WHERE FK_Utente = OLD.FK_Utente
      AND FK_Post IN (SELECT P.ID_Post FROM Post AS P WHERE P.FK_Gruppo = OLD.FK_Gruppo);
    -- VINCOLO (ID 41/28). Elimina gruppi creati da questo membro. L'istruzione
    -- sottostante ricopre anche l'eventualità in cui il gruppo sia vuoto (cioè
    -- il creatore l'ha abbandonato)
    DELETE FROM Gruppo WHERE FK_Creatore = OLD.FK_Utente AND ID_Gruppo = OLD.FK_Gruppo;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER MemberDelete
    BEFORE DELETE
    ON "membro"
    FOR EACH ROW
EXECUTE FUNCTION HandleMemberDeletion();

--
-- GESTIONE DELL'ELIMINAZIONE DI UN TAG
--
CREATE OR REPLACE FUNCTION HandleTagDeletion()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 42). Elimina le classificazioni dei gruppi relativi al tag eliminato
    DELETE FROM Classificazione WHERE FK_Tag = OLD.keyword;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER TagDelete
    BEFORE DELETE
    ON "tag"
    FOR EACH ROW
EXECUTE FUNCTION HandleTagDeletion();

--
-- CONTROLLI SUI DATI DELLE CLASSIFICAZIONI
--
CREATE OR REPLACE FUNCTION CheckClassificationData()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 7). Controlla unicità della classificazione
    IF EXISTS (SELECT 1 FROM Classificazione WHERE FK_Gruppo = NEW.FK_Gruppo AND FK_Tag = NEW.FK_Tag) THEN
        RAISE EXCEPTION USING HINT = 'Classificazione duplicata, esiste già.', ERRCODE = 'invalid_parameter_value';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER ClassificationDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "classificazione"
    FOR EACH ROW
EXECUTE FUNCTION CheckClassificationData();

--
-- CONTROLLI SUI DATI DELLE NOTIFICHE
--
CREATE OR REPLACE FUNCTION CheckNotificaitonData()
    RETURNS TRIGGER AS
$$
DECLARE
    group_id gruppo.ID_Gruppo%TYPE;
    silent   post.silenzioso%TYPE;
BEGIN
    -- Recupera l'ID del gruppo in cui è stato pubblicato il post
    SELECT G.ID_Gruppo
    INTO group_id
    FROM Post AS P
             JOIN Gruppo AS G ON (P.FK_Gruppo = G.ID_Gruppo)
    WHERE P.ID_Post = NEW.FK_Post
    LIMIT 1;

    -- VINCOLO (ID 27). Controlla che l'utente sia un membro del gruppo
    IF NOT EXISTS (SELECT 1 FROM Membro WHERE FK_Gruppo = group_id AND FK_Utente = NEW.FK_Utente) THEN
        RAISE EXCEPTION USING HINT = 'Utente non membro del gruppo in cui è stato pubblicato il post.', ERRCODE = 'invalid_parameter_value';
    END IF;

    SELECT silenzioso INTO silent FROM Post WHERE ID_Post = NEW.FK_Post LIMIT 1;
    -- VINCOLO (ID 26). Controlla che il post non sia silenzioso
    IF silent THEN
        RAISE EXCEPTION USING HINT = 'Il post di riferimento è silenzioso.', ERRCODE = 'invalid_parameter_value';
    END IF;

    -- VINCOLO (ID 43). Controlla che la notifica non si riferisca all'autore del post
    IF EXISTS (SELECT 1 FROM Post WHERE ID_Post = NEW.FK_Post AND FK_Utente = NEW.FK_Utente) THEN
        RAISE EXCEPTION USING HINT = 'Utente notificato creatore del post.', ERRCODE = 'invalid_parameter_value';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER NotificaitonDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "notifica"
    FOR EACH ROW
EXECUTE FUNCTION CheckNotificaitonData();

--
-- CONTROLLI SUI DATI DEI COMMENTI
--
CREATE OR REPLACE FUNCTION CheckCommentData()
    RETURNS TRIGGER AS
$$
DECLARE
    group_id gruppo.ID_Gruppo%TYPE;
    member   membro.FK_Utente%TYPE;
BEGIN
    -- Recupera l'ID del gruppo in cui è stato pubblicato il post commentato
    SELECT FK_Gruppo INTO group_id FROM Post WHERE ID_Post = NEW.FK_Post LIMIT 1;
    -- Blocca l'esecuzione se non è stato trovato il gruppo
    IF group_id IS NULL THEN
        RAISE EXCEPTION USING HINT = 'Non è stato trovato il gruppo relativo al post commentato.', ERRCODE = 'invalid_parameter_value';
    END IF;

    -- VINCOLO (ID 44). Controlla che l'utente sia un membro del gruppo in cui è stato pubblicato il post commentato
    SELECT FK_Utente INTO member FROM Membro WHERE FK_Gruppo = group_id AND FK_Utente = NEW.FK_Utente LIMIT 1;
    IF member IS NULL THEN
        RAISE EXCEPTION USING HINT =
                'Utente non membro del gruppo in cui è stato pubblicato il post commentato. Gruppo: ' ||
                group_id, ERRCODE = 'invalid_parameter_value';
    END IF;

    -- Vincolo (ID 19). Controllo che il padre sia diverso dall'ID del commento
    IF NEW.ID_Commento = NEW.FK_CommentoPadre THEN
        RAISE EXCEPTION USING HINT = 'Padre del commento identico al commento stesso.', ERRCODE = 'invalid_parameter_value';
    END IF;

    -- Vincolo (ID 33). La data di commento deve essere immutabile
    IF TG_OP = 'UPDATE' THEN
        NEW.data := OLD.data;
    -- Vincolo (ID 14). Controlla che data e ora di commento non siano oltre la data e ora corrente
    ELSIF TG_OP = 'INSERT' THEN
        IF NEW.data > CURRENT_TIMESTAMP THEN
            RAISE EXCEPTION USING HINT = 'Data di commento non valida: ' || NEW.data, ERRCODE = 'invalid_parameter_value';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER CommentDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "commento"
    FOR EACH ROW
EXECUTE FUNCTION CheckCommentData();

--
-- GESTIONE DELL'ELIMINAZIONE DI UN COMMENTO
--
CREATE OR REPLACE FUNCTION HandleCommentDeletion()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 25). Elimina i like e i sotto commenti del commento eliminato
    DELETE FROM LikeCommento WHERE FK_Commento = OLD.ID_Commento;
    DELETE FROM Commento WHERE FK_CommentoPadre = OLD.ID_Commento;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER CommentDelete
    BEFORE DELETE
    ON "commento"
    FOR EACH ROW
EXECUTE FUNCTION HandleCommentDeletion();

--
-- CONTROLLI SUI DATI DEI POST
--
CREATE OR REPLACE FUNCTION CheckPostData()
    RETURNS TRIGGER AS
$$
BEGIN
    -- Vincolo (ID 38). La data di modifica deve essere successiva alla data di pubblicazione
    IF NEW.ultima_mod IS NOT NULL AND NEW.ultima_mod < OLD.data THEN
        RAISE EXCEPTION USING HINT = 'Ultima modifica invalida: ' || NEW.ultima_mod, ERRCODE = 'invalid_parameter_value';
    END IF;

    -- VINCOLO (ID 18). Controlla URL della foto se presente
    IF NEW.tipo = 'photo' AND NOT NEW.contenuto ~ '^(http://|https://)' THEN
        RAISE EXCEPTION USING HINT = 'URL della foto deve iniziare con http:// o https://', ERRCODE = 'invalid_parameter_value';
    END IF;

    -- Vincolo (ID 12). Controlla che data e ora dell'ultima modifica non siano oltre la data e ora corrente
    IF NEW.ultima_mod > CURRENT_TIMESTAMP THEN
        RAISE EXCEPTION USING HINT = 'Data di modifica non valida: ' || NEW.ultima_mod, ERRCODE = 'invalid_parameter_value';
    END IF;

    -- Vincolo (ID 32). La data di pubblicazione deve essere immutabile
    IF TG_OP = 'UPDATE' THEN
        NEW.data := OLD.data;
    -- Vincolo (ID 11). Controlla che data e ora di pubblicazione non siano oltre la data e ora corrente
    ELSIF TG_OP = 'INSERT' THEN
        IF NEW.data > CURRENT_TIMESTAMP THEN
            RAISE EXCEPTION USING HINT = 'Data di pubblicazione non valida: ' || NEW.data, ERRCODE = 'invalid_parameter_value';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER PostDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "post"
    FOR EACH ROW
EXECUTE FUNCTION CheckPostData();

--
-- GESTIONE DELL'ELIMINAZIONE DI UN POST
--
CREATE OR REPLACE FUNCTION HandlePostDeletion()
    RETURNS TRIGGER AS
$$
DECLARE
    id post.ID_Post%TYPE := OLD.ID_Post;
BEGIN
    -- VINCOLO (ID 24). Elimina i like, commenti e notifiche del post eliminato
    DELETE FROM LikePost WHERE FK_Post = id;
    DELETE FROM Notifica WHERE FK_Post = id;
    DELETE FROM Commento WHERE FK_Post = id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER PostDelete
    BEFORE DELETE
    ON "post"
    FOR EACH ROW
EXECUTE FUNCTION HandlePostDeletion();

--
-- CONTROLLI GENERALI SUI DATI DEI LIKE
--
CREATE OR REPLACE FUNCTION CheckCommonLikeData()
    RETURNS TRIGGER AS
$$
BEGIN
    -- Vincolo (ID 34/35). La data di invio deve essere immutabile
    IF TG_OP = 'UPDATE' THEN
        NEW.data := OLD.data;
    -- Vincolo (ID 13/14). Controlla che data e ora di invio non siano oltre la data e ora corrente
    ELSIF TG_OP = 'INSERT' THEN
        IF NEW.data > CURRENT_TIMESTAMP THEN
            RAISE EXCEPTION USING HINT = 'Data di invio like non valida: ' || NEW.data, ERRCODE = 'invalid_parameter_value';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER LikePostCommonDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "likepost"
    FOR EACH ROW
EXECUTE FUNCTION CheckCommonLikeData();

CREATE OR REPLACE TRIGGER LikeCommentCommonDataValidationCheck
    BEFORE INSERT OR UPDATE
    ON "likecommento"
    FOR EACH ROW
EXECUTE FUNCTION CheckCommonLikeData();

--
-- CONTROLLI UNICITÀ DEI LIKE POST
--
CREATE OR REPLACE FUNCTION CheckLikePostUniqueness()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 3). Controlla che l'utente non abbia già messo like ad un post
    IF EXISTS (SELECT 1 FROM LikePost WHERE FK_Utente = NEW.FK_Utente AND FK_Post = NEW.FK_Post) THEN
        RAISE EXCEPTION USING HINT = 'Questo utente ha già messo like a questo post.', ERRCODE = 'invalid_parameter_value';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER LikePostUniquenessCheck
    BEFORE INSERT OR UPDATE
    ON "likepost"
    FOR EACH ROW
EXECUTE FUNCTION CheckLikePostUniqueness();

--
-- CONTROLLI UNICITÀ DEI LIKE COMMENTO
--
CREATE OR REPLACE FUNCTION CheckLikeCommentUniqueness()
    RETURNS TRIGGER AS
$$
BEGIN
    -- VINCOLO (ID 4). Controlla che l'utente non abbia già messo like ad un commento
    IF EXISTS (SELECT 1 FROM LikeCommento WHERE FK_Utente = NEW.FK_Utente AND FK_Commento = NEW.FK_Commento) THEN
        RAISE EXCEPTION USING HINT = 'Questo utente ha già messo like a questo commento.', ERRCODE = 'invalid_parameter_value';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER LikeCommentUniquenessCheck
    BEFORE INSERT OR UPDATE
    ON "likecommento"
    FOR EACH ROW
EXECUTE FUNCTION CheckLikeCommentUniqueness();

--
-- AGGIUNTA MEMBRO CREATORE AUTOMATICA
--
CREATE OR REPLACE FUNCTION AddGroupCreatorMember()
    RETURNS TRIGGER AS
$$
BEGIN
    -- Aggiungi un nuovo record nella tabella Membro assocciato al creatore
    INSERT INTO Membro VALUES (NEW.FK_Creatore, NEW.ID_Gruppo);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER GroupCreationMemberAddition
    AFTER INSERT
    ON "gruppo"
    FOR EACH ROW
EXECUTE FUNCTION AddGroupCreatorMember();

--
-- AGGIUNTA NOTIFICHE AUTOMATICA
--
CREATE OR REPLACE FUNCTION AddPostNotification()
    RETURNS TRIGGER AS
$$
DECLARE
    member Membro.FK_Utente%TYPE;
    -- Cursore per recuperare tutti i membri del gruppo eccetto l'autore del post
    members_cursor CURSOR FOR SELECT FK_Utente
                              FROM Membro
                              WHERE FK_Gruppo = NEW.FK_Gruppo
                                AND FK_Utente <> NEW.FK_Utente;
BEGIN
    IF NOT NEW.silenzioso THEN
        -- Apri il cursore
        OPEN members_cursor;
        LOOP
            FETCH members_cursor INTO member;
            -- Esci quando non ci sono altri risultati
            EXIT WHEN NOT FOUND;
            -- Aggiungi il una notifica per il membro del gruppo al relativo post pubblicato
            INSERT INTO Notifica VALUES (NEW.ID_Post, member);
        END LOOP;
        -- Chiudi il cursore
        CLOSE members_cursor;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER PostNotificationAddition
    AFTER INSERT
    ON "post"
    FOR EACH ROW
EXECUTE FUNCTION AddPostNotification();

--
-- PROCEDURA PER ANALIZZARE LA CRESCITA DELLE
-- ISCRIZIONI NEL CORSO DEI MESI E DEGLI ANNI
--
CREATE OR REPLACE PROCEDURE UserGrowth()
AS
$$
DECLARE
    startMonth          INT;
    endMonth            INT;
    startYear           INT;
    endYear             INT;
    monthLowerBound     INT;
    monthUpperBound     INT;
    prevRegisteredUsers INT;
    currRegisteredUsers INT;
    variation           INT;
    growth              VARCHAR(3);
BEGIN
    prevRegisteredUsers := 0;
    currRegisteredUsers := 0;
    -- Recupera e salva il mese e l'anno della prima iscrizione
    SELECT EXTRACT(MONTH FROM registrazione), EXTRACT(YEAR FROM registrazione)
    INTO startMonth, startYear
    FROM Utente
    ORDER BY registrazione
    LIMIT 1;
-- Recupera e salva il mese e l'anno corrente
    endMonth := EXTRACT(MONTH FROM CURRENT_DATE);
    endYear := EXTRACT(YEAR FROM CURRENT_DATE);
    -- Avviso riguardo al periodo analizzato
    RAISE NOTICE 'Analisi dal %/% al %/%', startMonth, startYear, endMonth, endYear;
    -- Per ogni anno dalla prima iscrizione a quello corrente...
    FOR y IN startYear..endYear
        LOOP
        -- Definisci il range (upper e lower bound) dei mesi da analizzare,
        -- controllando se l'anno attualmente in analisi è il primo, l'ultimo
        -- oppure ne è uno di mezzo
            monthLowerBound := CASE WHEN y = startYear THEN startMonth ELSE 1 END;
            monthUpperBound := CASE WHEN y = endYear THEN endMonth ELSE 12 END;
            -- Per ogni mese nel range definito prima...
            FOR m IN monthLowerBound..monthUpperBound
                LOOP
                    prevRegisteredUsers := currRegisteredUsers;
                    -- Conta gli utenti iscritti entro il mese 'm' e l'anno 'y'
                    SELECT COUNT(*)
                    INTO currRegisteredUsers
                    FROM Utente
                    WHERE EXTRACT(YEAR FROM registrazione) < y
                       OR (EXTRACT(YEAR FROM registrazione) = y AND EXTRACT(MONTH FROM registrazione) <= m)
                    LIMIT 1;
                    -- Calcola variazione, può essere anche negativa
                    variation := currRegisteredUsers - prevRegisteredUsers;
                    -- Calcola variazione percentuale. Nessuna variazione prevista per il primo mese del primo anno
                    growth := CASE
                                  WHEN m = startMonth AND y = startYear THEN '//'
                                  ELSE (100 * variation / prevRegisteredUsers) || '%' END;
                    -- Avviso riguardo ai dati del mese ed anno correnti
                    RAISE NOTICE '%/% - Utenti iscritti: % - Variazione iscritti: % - Variazione percentuale: %', m, y, currRegisteredUsers, variation, growth;
                END LOOP;
        END LOOP;
END;
$$ LANGUAGE plpgsql;

--
-- PROCEDURA PER ANALIZZARE LE NUOVE REGISTRAZIONI,
-- I POST PUBBLICATI, I GRUPPI CREATI, LIKE E COMMENTI
-- INVIATI IN UN DETERMINATO MESE ED ANNO
--
CREATE OR REPLACE PROCEDURE DataViewer(monthAnalyzed INT, yearAnalyzed INT)
AS
$$
DECLARE
    currDataCount  INT;
    tableParamName VARCHAR(50);
    tables         VARCHAR[]     := ARRAY ['Utente', 'Gruppo', 'Post', 'Commento', 'LikePost', 'LikeCommento'];
    query          VARCHAR(300);
    result         VARCHAR(1000) := '';
    cursor_counts  REFCURSOR;
    t              VARCHAR(50);
BEGIN
    RAISE NOTICE 'Analisi dal %/%', monthAnalyzed, yearAnalyzed;
    -- Per ogni tabella da analizzare salvata nell'array 'tables'
    FOREACH t IN ARRAY tables
        LOOP
            -- Definisci il parametro in cui è contenuta la data di registrazione/creazione/pubblicazione
            tableParamName :=
                    CASE WHEN t = 'Utente' THEN 'registrazione' WHEN t = 'Gruppo' THEN 'creazione' ELSE 'data' END;
            -- Definisci un comando dinamico tenendo conto della tabella e del parametro preso in analisi
            query := 'SELECT COUNT(*) AS num FROM ' || t || ' WHERE EXTRACT(MONTH FROM ' || tableParamName || ') = ' ||
                     monthAnalyzed || ' AND EXTRACT(YEAR FROM ' || tableParamName || ') = ' || yearAnalyzed;
            -- Apri il cursore per la query definita prima
            OPEN cursor_counts FOR EXECUTE query;
-- Per ogni record recuperato dalla query precedente..,
            LOOP
                -- Salva l'attributo COUNT(*) in 'currDataCount'
                FETCH cursor_counts INTO currDataCount;
                -- Esci quando non ci sono altri risultati
                EXIT WHEN NOT FOUND;
                -- Aggiungi il risultato alla stringa complessiva nel formato 'Tabella: nuovi_record'
                result := result || t || ': ' || currDataCount || ' ';
            END LOOP;
            -- Chiudi il cursore
            CLOSE cursor_counts;
        END LOOP;
    -- Rimuovi lo spazio vuoto alla fine della stringa
    result := RTRIM(result);
    -- Stampa l'analisi ricavata
    RAISE NOTICE 'Risultato: %', result;
END;
$$ LANGUAGE plpgsql;

--
-- ELIMINA TUTTI I POST SPECIFICATI IN UNA STRINGA
-- PASSATA IN INPUT, SEPARATI DA UNA VIRGOLA
--
CREATE OR REPLACE PROCEDURE PostsBulkDeletion(posts VARCHAR(1000))
AS
$$
DECLARE
    query   VARCHAR(2000) := 'DELETE FROM Post WHERE ID_Post IN (';
    pos     INT           := 0;
    old_pos INT           := 0;
BEGIN
    -- Leggi tutti gli ID dei post separati da virgola
    LOOP
        -- Recupera la posizione della virgola partendo dalla posizione 'old_pos', inizialmente 0
        pos := INSTR(posts, ',', old_pos + 1);
        -- Esci dal loop se non è stata trovata una virgola
        EXIT WHEN pos = 0;
        -- Ritaglia l'ID dal carattere 'old_pos' a 'pos' e aggiungilo alla query
        query := query || SUBSTR(posts, old_pos + 1, pos - old_pos - 1) || ',';
        -- Aggiorna la posizione della vecchia virgola con quella appena analizzata
        old_pos := pos;
    END LOOP;
    -- Sistema la parte finale della query
    query := RTRIM(query, ',');
    query := query || ')';
    -- Esegui il comando di eliminazione
    EXECUTE query;
END
$$ LANGUAGE plpgsql;

--
-- PARAMETRI:
--  GRUPPO IN CUI ANALIZZARE I POST
--  MESE DI PUBBLICAZIONE DEL POST
--  ANNO DI PUBBLICAZIONE DEL POST
-- TIPO DI RICERCA:
--  0 - POST CON IL MAGGIOR NUMERO DI LIKE
--  1 - POST CON IL MINOR NUMERO DI LIKE
--  2 - POST CON IL MAGGIOR NUMERO DI COMMENTI
--  3 - POST CON IL MINOR NUMERO DI COMMENTI
--
CREATE OR REPLACE FUNCTION GetSpecificPost(group_id Gruppo.ID_Gruppo%TYPE, monthAnalyzed INT, yearAnalyzed INT,
                                           tipo SMALLINT)
    RETURNS Post.ID_Post%TYPE
AS
$$
DECLARE
    query       VARCHAR(1000);
    tableName   VARCHAR(10);
    paramName   VARCHAR(20);
    orderType   VARCHAR(5);
    post_id     Post.ID_Post%TYPE;
    post_cursor REFCURSOR;
BEGIN
    -- Recupera la tabella in cercare in base al tipo di ricerca
    tableName := CASE WHEN tipo < 2 THEN 'LikePost' ELSE 'Commento' END;
    -- Recupera il parametro da analizzare in base al tipo di ricerca
    paramName := CASE WHEN tipo < 2 THEN 'ID_Post' ELSE 'ID_Commento' END;
    -- Recupera il tipo di ordinamento in base al tipo di ricerca
    orderType := CASE WHEN tipo % 2 = 0 THEN 'DESC' ELSE 'ASC' END;
    -- Query dinamica da eseguire per cercare il post
    query := 'SELECT id FROM (
        SELECT P.ID_Post AS id COUNT(T.' || paramName || ') AS count FROM ' || tableName || ' AS T JOIN Post AS P ON T.FK_Post = P.ID_Post
        WHERE P.FK_Gruppo = ' || group_id || ' AND EXTRACT(MONTH FROM data) = ' || monthAnalyzed ||
             ' AND EXTRACT(YEAR FROM data) = ' || yearAnalyzed || '
        GROUP BY (P.ID_Post)) ORDER BY count ' || orderType || ' LIMIT 1';
    -- Apri il cursore per la query definita prima
    OPEN post_cursor FOR EXECUTE query;
    -- Per ogni record recuperato dalla query precedente..,
    LOOP
        -- Salva l'ID del post
        FETCH post_cursor INTO post_id;
        -- Esci quando non ci sono altri risultati
        EXIT WHEN NOT FOUND;
    END LOOP;
    -- Chiudi il cursore
    CLOSE post_cursor;
    -- Ritorna l'ID del post trovato
    RETURN post_id;
END
$$ LANGUAGE plpgsql;

--
-- RECUPERA IL NUMERO MEDIO DI POST E COMMENTI PUBBLICATI
-- NEI GRUPPI IN UN DETERMINATO MESE ED ANNO
--
CREATE OR REPLACE FUNCTION GetAverageContents(
    IN monthAnalyzed INT,
    IN yearAnalyzed INT,
    OUT avg_posts INT,
    OUT avg_comments INT)
AS
$$
BEGIN
    SELECT AVG(posts_count.posts)
    INTO avg_posts
    FROM (SELECT COUNT(P.ID_Post) AS posts
          FROM Post AS P
          WHERE EXTRACT(MONTH FROM P.data) = monthAnalyzed
            AND EXTRACT(MONTH FROM P.data) = yearAnalyzed
          GROUP BY (P.FK_Gruppo)) as posts_count;
    SELECT AVG(comments_count.comments)
    INTO avg_comments
    FROM (SELECT COUNT(C.ID_Commento) AS comments
          FROM Commento AS C
                   JOIN Post AS P ON C.FK_Post = P.ID_Post
          WHERE EXTRACT(MONTH FROM C.data) = monthAnalyzed
            AND EXTRACT(MONTH FROM C.data) = yearAnalyzed
          GROUP BY (P.FK_Gruppo)) as comments_count;
END
$$ LANGUAGE plpgsql;