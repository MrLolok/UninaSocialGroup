package org.unina.project.social.managers.extractors;

import org.unina.project.social.entities.users.DefaultUser;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.enums.Gender;
import org.unina.project.social.managers.AbstractPersistentEntityExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class BasicUsersResultSetExtractor extends AbstractPersistentEntityExtractor<User> {
    @Override
    protected void load(Set<User> currentLoadedEntities, ResultSet set) throws SQLException, IllegalArgumentException {
        int id = set.getInt("ID_Utente");
        User user = new DefaultUser(id);
        user.setName(set.getString("nome"));
        user.setSurname(set.getString("cognome"));
        user.setEmail(set.getString("email"));
        user.setPassword(set.getString("password"));
        user.setBiography(set.getString("biografia"));
        user.setAvatar(set.getString("avatar"));
        user.setAge(set.getInt("eta"));
        user.setRegistration(set.getTimestamp("registrazione"));
        Object rawGenderName = set.getObject("genere");
        if (rawGenderName != null) {
            Optional<Gender> gender = Gender.get(rawGenderName.toString());
            gender.ifPresent(user::setGender);
        }
        currentLoadedEntities.add(user);
    }
}