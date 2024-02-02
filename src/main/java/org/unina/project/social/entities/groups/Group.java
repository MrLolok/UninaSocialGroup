package org.unina.project.social.entities.groups;

import org.unina.project.social.entities.IdentifiableEntity;
import org.unina.project.social.entities.tags.Tag;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.enums.GroupType;

import java.sql.Timestamp;
import java.util.Set;

public interface Group extends IdentifiableEntity<Integer> {
    /**
     * Restituisce l'utente che ha creato il gruppo.
     * @return creatore del gruppo
     */
    User getCreator();

    /**
     * Restituisce il nome del gruppo.
     * @return nome del gruppo
     */
    String getName();

    /**
     * Restituisce la descrizione del gruppo.
     * @return descrizione del gruppo
     */
    String getDescription();

    /**
     * Restituisce il tipo di gruppo.
     * @return tipo di gruppo
     */
    GroupType getType();

    /**
     * Restituisce data e ora in cui il gruppo è stato creato.
     * @return data e ora di creazione del gruppo
     */
    Timestamp getCreation();

    /**
     * Restituisce l'insieme di membri nel gruppo.
     * @return membri del gruppo
     */
    Set<User> getMembers();

    /**
     * Restituisce l'insieme di utenti che hanno richiesto di unirsi al gruppo.
     * @return utenti che hanno richiesto di unirsi al gruppo
     */
    Set<User> getRequesters();

    /**
     * Restituisce l'insieme di tag associati al gruppo.
     * @return tag associati al gruppo
     */
    Set<Tag> getTags();

    /**
     * Imposta il creatore del gruppo.
     * @param creator del gruppo
     */
    void setCreator(User creator);

    /**
     * Imposta il nome del gruppo.
     * @param name del gruppo
     */
    void setName(String name);

    /**
     * Imposta la descrizione del gruppo.
     * @param description del gruppo
     */
    void setDescription(String description);

    /**
     * Imposta il tipo di gruppo.
     * @param type del gruppo
     */
    void setType(GroupType type);

    /**
     * Imposta data e ora di creazione del gruppo.
     * @param creation del gruppo
     */
    void setCreation(Timestamp creation);

    /**
     * Imposta l'insieme di membri del gruppo.
     * @param members del gruppo
     */
    void setMembers(Set<User> members);

    /**
     * Imposta l'insieme di utenti che hanno richiesto di unirsi al gruppo.
     * @param requesters del gruppo
     */
    void setRequesters(Set<User> requesters);

    /**
     * Imposta l'insieme di tag associati al gruppo.
     * @param tags del gruppo
     */
    void setTags(Set<Tag> tags);

    /**
     * Aggiunge un tag all'insieme di tag del gruppo.
     * @param keyword del tag da aggiungere
     */
    void addTag(String keyword);

    /**
     * Rimuove un tag dall'insieme di tag del gruppo.
     * @param keyword del tag da rimuovere
     */
    void removeTag(String keyword);

    /**
     * Verifica se un utente è un membro del gruppo.
     * @param user da verificare
     * @return true se l'utente è un membro del gruppo
     */
    boolean isMember(User user);

    /**
     * Verifica se un utente ha richiesto di unirsi al gruppo.
     * @param user da verificare
     * @return true se l'utente ha richiesto di unirsi al gruppo
     */
    boolean isRequester(User user);
}
