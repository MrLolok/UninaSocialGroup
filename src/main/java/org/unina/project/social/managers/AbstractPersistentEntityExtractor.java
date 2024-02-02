package org.unina.project.social.managers;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unina.project.database.query.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Estrattore astratto di entità della piattaforma da un {@link ResultSet}.
 * @param <T> tipo di entità
 */
@RequiredArgsConstructor
public abstract class AbstractPersistentEntityExtractor<T> implements ResultSetExtractor<Set<T>> {
    private final static Logger LOGGER = Logger.getLogger("UninaSocialGroup-EntityExtractor");

    /**
     * Per ogni record contenuto nel {@link ResultSet}, recupera i dati
     * dell'entità tramite il metodo {@link #load(Set, ResultSet)}, così
     * da crearne una nuova e restituirla.
     * @param set da operare
     * @return set di entità caricate
     */
    @Override
    public @Nullable Set<T> extract(@NotNull ResultSet set) {
        Set<T> entities = new HashSet<>();
        try {
            while (set.next())
                load(entities, set);
        } catch (IllegalArgumentException e) {
            LOGGER.warning(String.format("Impossibile costruire l'entità dal %s: %s", getClass().getSimpleName(), e.getMessage()));
        } catch (SQLException e) {
            LOGGER.warning(String.format("Impossibile recuperare i dati dell'entità dal %s: %s", getClass().getSimpleName(), e.getMessage()));
        }
        return entities;
    }

    /**
     * Opera un {@link ResultSet} per estrarre i dati di un'entità.
     * @param currentLoadedEntities insieme corrente di entità caricate.
     * @param set da operare
     * @throws SQLException nel caso si presentino errori durante operazioni SQL
     * @throws IllegalArgumentException nel caso siano presenti parametri invalidi per costruire un'entità
     */
    protected abstract void load(Set<T> currentLoadedEntities, ResultSet set) throws SQLException, IllegalArgumentException;
}
