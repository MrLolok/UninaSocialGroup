package org.unina.project.social.managers.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unina.project.database.operations.Database;
import org.unina.project.database.query.StatementMatch;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.enums.Gender;
import org.unina.project.social.managers.DAO;
import org.unina.project.social.managers.extractors.BasicUsersResultSetExtractor;

import java.sql.Types;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class UsersDAO extends DAO<User> {
    public UsersDAO(Database database) {
        super(database, new BasicUsersResultSetExtractor());
    }

    public Set<User> getAll() {
        return getAll("SELECT * FROM utente");
    }

    public Optional<User> get(int id) {
        return get("SELECT * FROM utente WHERE id_utente = ?", new StatementMatch(1, id, Types.INTEGER));
    }

    public Optional<User> get(String email, String password) {
        return get("SELECT * FROM utente WHERE email = ? AND password = ?",
                new StatementMatch(1, email, Types.VARCHAR),
                new StatementMatch(2, password, Types.VARCHAR));
    }

    public Optional<User> create(@NotNull String name, @NotNull String surname, @NotNull String email, @NotNull String password, int age, @Nullable Gender gender, @Nullable String biography, @Nullable String avatar) {
        return create("INSERT INTO utente (nome, cognome, email, password, eta, genere, biografia, avatar) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_utente, nome, cognome, email, password, eta, biografia, avatar, genere, registrazione",
                new StatementMatch(1, name, Types.VARCHAR),
                new StatementMatch(2, surname, Types.VARCHAR),
                new StatementMatch(3, email, Types.VARCHAR),
                new StatementMatch(4, password, Types.VARCHAR),
                new StatementMatch(5, age, Types.INTEGER),
                new StatementMatch(6, gender == null ? null : gender.getSqlParamName(), gender == null ? Types.NULL : Types.OTHER),
                new StatementMatch(7, biography, biography == null ? Types.NULL : Types.VARCHAR),
                new StatementMatch(8, avatar, avatar == null ? Types.NULL : Types.VARCHAR));
    }

    public CompletableFuture<Integer> update(User user) {
        return update("UPDATE utente SET nome = ?, cognome = ?, email = ?, password = ?, eta = ?, genere = ?, biografia = ?, avatar = ? WHERE id_utente = ?", user);
    }

    public CompletableFuture<Integer> delete(User user) {
        return delete("DELETE FROM utente WHERE id_utente = ?", new StatementMatch(1, user.getId(), Types.INTEGER));
    }

    @Override
    protected StatementMatch[] getUpdatedMatches(User user) {
        return new StatementMatch[] {
                new StatementMatch(1, user.getName(), Types.VARCHAR),
                new StatementMatch(2, user.getSurname(), Types.VARCHAR),
                new StatementMatch(3, user.getEmail(), Types.VARCHAR),
                new StatementMatch(4, user.getPassword(), Types.VARCHAR),
                new StatementMatch(5, user.getAge(), Types.INTEGER),
                new StatementMatch(6, user.getGender() == null ? null : user.getGender().getSqlParamName(), user.getGender() == null ? Types.NULL : Types.OTHER),
                new StatementMatch(7, user.getBiography(), user.getBiography() == null ? Types.NULL : Types.VARCHAR),
                new StatementMatch(8, user.getAvatar(), user.getAvatar() == null ? Types.NULL : Types.VARCHAR),
                new StatementMatch(9, user.getId(), Types.INTEGER)
        };
    }
}
