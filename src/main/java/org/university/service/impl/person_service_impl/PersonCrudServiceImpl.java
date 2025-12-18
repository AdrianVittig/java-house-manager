package org.university.service.impl.person_service_impl;

import org.university.dao.person_dao.PersonCrudDao;
import org.university.dao.person_dao.PersonMapperDao;
import org.university.dto.PersonDto;
import org.university.entity.Person;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.service.contract.person_service.PersonCrudService;

import java.util.List;

public class PersonCrudServiceImpl implements PersonCrudService {
    private final PersonCrudDao personDao = new PersonCrudDao();
    private final PersonMapperDao personMapper = new PersonMapperDao();
    @Override
    public void createPerson(Person person) throws DAOException, NotFoundException {
        if(person == null){
            throw new IllegalArgumentException("Person cannot be null");
        }

        if(person.getFirstName() == null || person.getLastName() == null){
            throw new IllegalArgumentException("Person first name and last name cannot be null");
        }

        if(person.getAge() <= 0){
            throw new IllegalArgumentException("Person age cannot be less than or equal to 0");
        }

        personDao.createPerson(person);
    }

    @Override
    public PersonDto getPersonById(Long id) throws DAOException, NotFoundException {
        Person person = personDao.getPersonById(id);
        if(person == null){
            throw new NotFoundException("Person with id " + id + " does not exist");
        }
        return personMapper.toDto(person);
    }

    @Override
    public List<PersonDto> getAllPeople() {
        return personDao.getAllPeople()
                .stream()
                .map(personMapper::toDto)
                .toList();
    }

    @Override
    public void updatePerson(Person person) throws DAOException, NotFoundException {
        if(person == null || person.getId() == null){
            throw new IllegalArgumentException("Person cannot be null");
        }
        personDao.updatePerson(person.getId(), person);
    }

    @Override
    public void deletePerson(Long id) throws DAOException, NotFoundException {
        PersonDto person = getPersonById(id);
        if(person == null){
            throw new NotFoundException("Person with id " + id + " does not exist");
        }
        personDao.deletePerson(id);
    }
}
