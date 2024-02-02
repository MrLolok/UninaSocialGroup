package org.unina.project.social.entities.users;

import lombok.Getter;
import lombok.Setter;
import org.unina.project.social.entities.AbstractIdentifiableEntity;
import org.unina.project.social.enums.Gender;

import java.sql.Timestamp;

@Getter @Setter
public class DefaultUser extends AbstractIdentifiableEntity<Integer> implements User {
    private String name, surname, email, password, biography, avatar;
    private int age;
    private Gender gender;
    private Timestamp registration;

    public DefaultUser(int id) {
        super(id);
    }

    @Override
    public String getFullName() {
        return String.format("%s %s", name, surname);
    }
}
