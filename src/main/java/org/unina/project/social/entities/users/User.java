package org.unina.project.social.entities.users;

import org.unina.project.social.entities.IdentifiableEntity;
import org.unina.project.social.enums.Gender;

import java.sql.Timestamp;

public interface User extends IdentifiableEntity<Integer> {
    /**
     * Restituisce il nome dell'utente.
     * @return nome dell'utente
     */
    String getName();

    /**
     * Restituisce il cognome dell'utente.
     * @return cognome dell'utente
     */
    String getSurname();

    /**
     * Restituisce l'indirizzo email dell'utente.
     * @return indirizzo email dell'utente
     */
    String getEmail();

    /**
     * Restituisce la password dell'utente.
     * @return password dell'utente
     */
    String getPassword();

    /**
     * Restituisce la biografia dell'utente.
     * @return biografia dell'utente
     */
    String getBiography();

    /**
     * Restituisce l'avatar dell'utente.
     * @return avatar dell'utente
     */
    String getAvatar();

    /**
     * Restituisce l'età dell'utente.
     * @return età dell'utente
     */
    int getAge();

    /**
     * Restituisce il genere dell'utente.
     * @return genere dell'utente
     */
    Gender getGender();

    /**
     * Restituisce data e ora di registrazione dell'utente.
     * @return data e ora di registrazione dell'utente
     */
    Timestamp getRegistration();

    /**
     * Imposta il nome dell'utente.
     * @param name da impostare
     */
    void setName(String name);

    /**
     * Imposta il cognome dell'utente.
     * @param surname da impostare
     */
    void setSurname(String surname);

    /**
     * Imposta l'indirizzo email dell'utente.
     * @param email da impostare
     */
    void setEmail(String email);

    /**
     * Imposta la password dell'utente.
     * @param password da impostare
     */
    void setPassword(String password);

    /**
     * Imposta la biografia dell'utente.
     * @param biography da impostare
     */
    void setBiography(String biography);

    /**
     * Imposta l'avatar dell'utente.
     * @param avatar da impostare
     */
    void setAvatar(String avatar);

    /**
     * Imposta l'età dell'utente.
     * @param age da impostare
     */
    void setAge(int age);

    /**
     * Imposta il genere dell'utente.
     * @param gender da impostare
     */
    void setGender(Gender gender);

    /**
     * Imposta il timestamp di registrazione dell'utente.
     * @param registration da impostare
     */
    void setRegistration(Timestamp registration);

    /**
     * Restituisce il nome completo dell'utente (nome + cognome).
     * @return nome completo dell'utente
     */
    String getFullName();
}
