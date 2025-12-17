package org.university;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Person;

public class Main {
    public static void main(String[] args) {
        Person person = new Person("Adrian", "Kowalski", 17);

        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(person);
            transaction.commit();
        }
    }
}