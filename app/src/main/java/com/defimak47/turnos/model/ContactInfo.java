package com.defimak47.turnos.model;

/**
 * Created by jzuriaga on 27/12/14.
 */
public class ContactInfo {

    public static final String NAME_PREFIX = "Name_";
    public static final String LOGIN_PREFIX = "Login_";
    public static final String EMAIL_PREFIX = "email_";
    public static final String POSITION_PREFIX = "Position_";

    protected String login;
    protected String name;
    protected String email;
    protected String position;
    protected String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * getter for property name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * setter for property name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for property position.
     * @return
     */
    public String getPosition() {
        return position;
    }

    /**
     * setter for property position.
     *
     * @param position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public static class Projection {
        public static final String NAME = "name";
        public static final String LOGIN = "login";
        public static final String ALIAS = "alias";
        public static final String POSITION = "position";
        public static final String EMAIL = "email";
    }
}