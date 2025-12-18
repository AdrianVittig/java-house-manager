package org.university.service.contract.person_service;

import org.university.dto.PersonDto;
import org.university.entity.Person;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public interface PersonCrudService {
    void createPerson(Person person) throws DAOException, NotFoundException;
    PersonDto getPersonById(Long id) throws DAOException, NotFoundException;
    List<PersonDto> getAllPeople();
    void updatePerson(Person person) throws DAOException, NotFoundException;
    void deletePerson(Long id) throws DAOException, NotFoundException;
}
