package org.unina.project.social.sessions;

import org.jetbrains.annotations.Nullable;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.users.User;

/**
 * Questa classe rappresenta una sessione di utilizzo della piattaforma.
 * Da qui è possibile recuperare l'utente che ha effettuato l'accesso in
 * questa sessione e il gruppo attualmente mostrato/gestito nell'interfaccia.
 */
public interface Session {
    /**
     * Istanza dalla Session globale
     */
    Session GLOBAL_SESSION = new GlobalSession();

    /**
     * Ottieni l'istanza della Session globale
     * @return istanza globale
     */
    static Session getGlobalSession() {
        return GLOBAL_SESSION;
    }

    /**
     * Ottieni l'utente che ha effettuato l'accesso.
     * @return utente
     */
    @Nullable User getUser();

    /**
     * Ottieni il gruppo attualmente visualizzato o gestito.
     * @return gruppo
     */
    @Nullable Group getGroup();

    /**
     * Imposta l'utente associato a questa sessione.
     * @param user da impostare
     */
    void setUser(@Nullable User user);


    /**
     * Imposta il gruppo associato a questa sessione.
     * @param group da impostare
     */
    void setGroup(@Nullable Group group);

    /**
     * Reimposta con i valori default questa sessione.
     */
    void reset();
}
