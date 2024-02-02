package org.unina.project.social.managers;

import lombok.Getter;
import org.intellij.lang.annotations.Language;
import org.unina.project.database.operations.AsyncDatabase;
import org.unina.project.database.operations.Database;
import org.unina.project.database.provider.DatabaseProvider;
import org.unina.project.database.query.ResultSetExtractor;
import org.unina.project.database.query.StatementMatch;
import org.unina.project.database.query.impl.MatchesPreparedStatementSetter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Classe per accedere tramite il database alle informazioni di entità
 * presenti in questa piattaforma ed eventualmente modificarle.
 * @param <T> tipo di entità
 */
@Getter
public abstract class DAO<T> {
    private final Database database;
    private final AsyncDatabase asyncDatabase;
    private final ResultSetExtractor<Set<T>> extractor;

    protected DAO(Database database, ResultSetExtractor<Set<T>> extractor) {
        this.database = database;
        this.asyncDatabase = DatabaseProvider.getProvider().newAsyncDatabase(database);
        this.extractor = extractor;
    }

    /**
     * Recupera tutte le entità.
     * @param sql comando da eseguire
     * @param matches di ricerca
     * @return entità trovate
     */
    public Set<T> getAll(@Language("SQL") String sql, StatementMatch... matches) {
        return database.query(sql, new MatchesPreparedStatementSetter(matches), extractor);
    }

    /**
     * Recupera un'entità specifica.
     * @param sql comando da eseguire
     * @param matches di ricerca
     * @return entità trovata
     */
    public Optional<T> get(@Language("SQL") String sql, StatementMatch... matches) {
        Set<T> result = database.query(sql, new MatchesPreparedStatementSetter(matches), extractor);
        if (result == null)
            throw new IllegalStateException("Non è stato possibile recuperare l'entità dal database.");
        return result.stream().findFirst();
    }

    /**
     * Crea una nuova entità e salvala nel database.
     * @param sql comando da eseguire
     * @param matches di creazione
     * @return entità creata
     */
    public Optional<T> create(@Language("SQL") String sql, StatementMatch... matches) {
        return get(sql, matches);
    }

    /**
     * Aggiorna entità nel database.
     * @param sql comando da eseguire
     * @param entity da aggiornare
     * @param matches di ricerca (utili quando dall'oggetto "entity" non sono più recuperabili, come nella classe {@link org.unina.project.social.managers.impl.TagsDAO})
     * @return numero di record aggiornati
     */
    public CompletableFuture<Integer> update(@Language("SQL") String sql, T entity, StatementMatch... matches) {
        List<StatementMatch> list = new ArrayList<>(Arrays.stream(getUpdatedMatches(entity)).toList());
        list.addAll(Arrays.stream(matches).toList());
        return asyncDatabase.update(sql, new MatchesPreparedStatementSetter(list.toArray(StatementMatch[]::new)));
    }

    /**
     * Elimina entità dal database.
     * @param sql comando da eseguire
     * @param matches di ricerca
     * @return numero di record eliminati
     */
    public CompletableFuture<Integer> delete(@Language("SQL") String sql, StatementMatch... matches) {
        return asyncDatabase.update(sql, new MatchesPreparedStatementSetter(matches));
    }

    /**
     * Ottieni gli {@link StatementMatch} default associati a un'entità.
     * @param entity da cui recuperare i match
     * @return array di match
     */
    protected abstract StatementMatch[] getUpdatedMatches(T entity);
}
