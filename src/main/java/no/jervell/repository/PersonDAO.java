package no.jervell.repository;

import no.jervell.domain.Person;

import java.io.IOException;
import java.util.List;

public interface PersonDAO {
    public void persist() throws IOException;

    public List<Person> getPersonList();
}
