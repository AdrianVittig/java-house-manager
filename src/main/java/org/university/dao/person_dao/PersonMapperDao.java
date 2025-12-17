package org.university.dao.person_dao;

import org.university.dto.PersonDto;
import org.university.entity.Person;

public class PersonMapperDao {
    public PersonDto toListDto(Person person){
        PersonDto personDto = new PersonDto();
        personDto.setId(person.getId());
        personDto.setFirstName(person.getFirstName());
        personDto.setLastName(person.getLastName());
        personDto.setAge(person.getAge());
        return personDto;
    }
}
