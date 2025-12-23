package org.university.dao.payment_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Invoice;
import org.university.entity.Payment;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.util.PaymentStatus;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class PaymentCrudDao {
    public void createPayment(Payment payment){
        Session session = null;
        Transaction transaction = null;
        try{
            if(payment == null){
                throw new IllegalArgumentException("Payment cannot be null");
            }
            if(payment.getInvoice() == null || payment.getInvoice().getId() == null){
                throw new IllegalArgumentException("Invoice cannot be null");
            }

            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Invoice managed = session.find(Invoice.class, payment.getInvoice().getId());
            if(managed == null){
                throw new NotFoundException("Invoice with id " + payment.getInvoice().getId() + " does not exist");
            }
            if(managed.getPaymentStatus() == PaymentStatus.PAID){
                throw new IllegalArgumentException("Invoice is already paid");
            }



            Long count = session.createQuery(
                    "SELECT count(p) FROM Payment p " +
                            "WHERE p.invoice.id = :invoiceId", Long.class)
                            .setParameter("invoiceId", payment.getInvoice().getId())
                                    .getSingleResult();

            if(count != null && count > 0){
                throw new IllegalArgumentException("Invoice already has payments");
            }

            if(managed.getTotalAmount() == null){
                throw new IllegalArgumentException("Invoice total amount cannot be null");
            }

            payment.setInvoice(managed);
            payment.setAmount(managed.getTotalAmount());
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());

            session.persist(payment);
            managed.setPaymentStatus(PaymentStatus.PAID);
            managed.setPayment(payment);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while creating payment: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Payment getPaymentById(Long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT p FROM Payment p " +
                            "LEFT JOIN FETCH p.invoice i " +
                            "LEFT JOIN FETCH i.apartment a " +
                            "WHERE p.id = :id",
                    Payment.class
            )
                    .setParameter("id", id)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        }catch(Exception e){
            throw new DAOException("Error while getting payment with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Payment getPaymentByInvoiceId(Long invoiceId){
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT p FROM Payment p " +
                                    "LEFT JOIN FETCH p.invoice i " +
                                    "LEFT JOIN FETCH i.apartment a " +
                                    "LEFT JOIN FETCH a.building b " +
                                    "LEFT JOIN FETCH b.employee e " +
                                    "LEFT JOIN FETCH e.company c " +
                                    "WHERE i.id = :invoiceId",
                            Payment.class
                    )
                    .setParameter("invoiceId", invoiceId)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            throw new DAOException("Error while getting payment for invoice id: " + invoiceId, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public List<Payment> getPaymentsByApartmentId(Long apartmentId){
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.invoice.apartment.id = :apartmentId " +
                                    "ORDER BY p.paidAt DESC",
                            Payment.class
                    )
                    .setParameter("apartmentId", apartmentId)
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Error while getting payments for apartment id: " + apartmentId, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public List<Payment> getPaymentsByBuildingId(Long buildingId){
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.invoice.apartment.building.id = :buildingId " +
                                    "ORDER BY p.paidAt DESC",
                            Payment.class
                    )
                    .setParameter("buildingId", buildingId)
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Error while getting payments for building id: " + buildingId, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public List<Payment> getPaymentsByBuildingAndMonth(Long buildingId, YearMonth billingDate){
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.invoice.apartment.building.id = :buildingId " +
                                    "AND p.invoice.billingMonth = :billingDate " +
                            "ORDER BY p.paidAt DESC",
                            Payment.class
                    )
                    .setParameter("buildingId", buildingId)
                    .setParameter("billingDate", billingDate)
                    .getResultList();
        } catch (Exception e) {
            throw new DAOException("Error while getting payments for building id: " + buildingId, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public List<Payment> getAllPayments(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT p FROM Payment p " +
                            "ORDER BY p.paidAt DESC ",
                    Payment.class)
                    .getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting all payments: ", e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void updatePayment(Long id, Payment payment){
        Session session = null;
        Transaction transaction = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Payment updatedPayment = session.find(Payment.class, id);
            if(updatedPayment == null){
                throw new NotFoundException("Payment with id " + id + " does not exist");
            }
            if(payment != null){
                if(payment.getAmount() != null) updatedPayment.setAmount(payment.getAmount());
                if(payment.getPaymentStatus() != null) updatedPayment.setPaymentStatus(payment.getPaymentStatus());
                if(payment.getPaidAt() != null) updatedPayment.setPaidAt(payment.getPaidAt());
            }

            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while updating payment with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void deletePayment(Long id){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Payment payment = session.find(Payment.class, id);
            if(payment == null){
                throw new NotFoundException("Payment with id " + id + " does not exist");
            }
            Invoice invoice = payment.getInvoice();
            invoice.setPaymentStatus(PaymentStatus.NOT_PAID);
            session.remove(payment);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting payment with id: " + id, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
}
