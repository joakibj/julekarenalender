package no.jervell.util;

import no.jervell.domain.Person;

public class PersonBuilder implements Builder<Person> {
    private String name;
    private String picture;
    private int day;

    public static PersonBuilder getPersonBuilder() {
        return new PersonBuilder();
    }

    public PersonBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder picture(String picture) {
        this.picture = picture;
        return this;
    }

    public PersonBuilder day(int day) {
        this.day = day;
        return this;
    }

    public Person build() {
        Person person = new Person();
        person.setName(name);
        person.setPicture(picture);
        person.setDay(day);
        return person;
    }
}
