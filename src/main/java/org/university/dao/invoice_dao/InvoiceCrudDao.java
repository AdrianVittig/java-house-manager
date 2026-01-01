package org.university.dao.invoice_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.entity.Invoice;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.time.YearMonth;
import java.util.List;

public class InvoiceCrudDao {

    public void createInvoice(Invoice invoice) {
        Session session = null;
        Transaction transaction = null;

        try {
            if (invoice == null) {
                throw new IllegalArgumentException("Invoice cannot be null");
            }
            if (invoice.getApartment() == null || invoice.getApartment().getId() == null) {
                throw new IllegalArgumentException("Invoice apartment cannot be null");
            }
            if (invoice.getBillingMonth() == null) {
                throw new IllegalArgumentException("Billing month cannot be null");
            }
            if (invoice.getDueDate() == null) {
                throw new IllegalArgumentException("Due date cannot be null");
            }
            if (invoice.getTotalAmount() == null) {
                throw new IllegalArgumentException("Total amount cannot be null");
            }
            if (invoice.getPaymentStatus() == null) {
                throw new IllegalArgumentException("Payment status cannot be null");
            }

            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Apartment managedApartment = session.find(Apartment.class, invoice.getApartment().getId());
            if (managedApartment == null) {
                throw new NotFoundException("Apartment with id " + invoice.getApartment().getId() + " does not exist");
            }

            invoice.setApartment(managedApartment);

            session.persist(invoice);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Error while creating invoice: ", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public Invoice getInvoiceById(Long id) {
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT i FROM Invoice i " +
                                    "LEFT JOIN FETCH i.apartment a " +
                                    "WHERE i.id = :id",
                            Invoice.class
                    )
                    .setParameter("id", id)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (NotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new DAOException("Error while getting invoice with id: " + id, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public List<Invoice> getInvoicesByApartment(Long apartmentId) {
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT i FROM Invoice i " +
                                    "WHERE i.apartment.id = :apartmentId " +
                                    "ORDER BY i.billingMonth DESC",
                            Invoice.class
                    )
                    .setParameter("apartmentId", apartmentId)
                    .getResultList();
        }catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Error while getting invoices for apartment id: " + apartmentId, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public List<Invoice> getInvoicesByBuilding(Long buildingId) {
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT i FROM Invoice i " +
                                    "WHERE i.apartment.building.id = :buildingId " +
                                    "ORDER BY i.billingMonth DESC",
                            Invoice.class
                    )
                    .setParameter("buildingId", buildingId)
                    .getResultList();
        }catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Error while getting invoices for building id: " + buildingId, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public List<Invoice> getAllInvoices() {
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT i FROM Invoice i ORDER BY i.id ASC",
                    Invoice.class
            ).getResultList();
        } catch (Exception e) {
            throw new DAOException("Error while getting all invoices: ", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public Invoice getInvoiceByApartmentAndMonth(Long apartmentId, YearMonth billingMonth) {
        Session session = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT i FROM Invoice i " +
                                    "LEFT JOIN FETCH i.apartment a " +
                                    "WHERE a.id = :apartmentId AND i.billingMonth = :billingMonth",
                            Invoice.class
                    )
                    .setParameter("apartmentId", apartmentId)
                    .setParameter("billingMonth", billingMonth)
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (NotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new DAOException(
                    "Error while getting invoice for apartmentId " + apartmentId + " and month " + billingMonth,
                    e
            );
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public Invoice getInvoiceWithDetails(Long id) {
        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT i FROM Invoice i " +
                                    "LEFT JOIN FETCH i.apartment " +
                                    "LEFT JOIN FETCH i.payment " +
                                    "WHERE i.id = :id", Invoice.class
                    ).setParameter("id", id)
                    .getResultList().stream().findFirst().orElse(null);
        } catch (NotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new DAOException("Error while getting invoice details id=" + id, e);
        }finally {
            if(session != null && session.isOpen()) session.close();
        }
    }

    public List<Invoice> getInvoicesByBuildingAndMonth(Long buildingId, YearMonth billingMonth) {
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT i from Invoice i " +
                            "JOIN FETCH i.apartment a " +
                            "JOIN FETCH a.building b " +
                            "WHERE b.id = :buildingId " +
                            "AND i.billingMonth = :billingMonth " +
                            "ORDER BY a.id ", Invoice.class)
                    .setParameter("buildingId", buildingId)
                    .setParameter("billingMonth", billingMonth)
                    .getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting invoices for building id: " + buildingId + " and month: " + billingMonth, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void updateInvoice(Long id, Invoice invoice) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Invoice managed = session.find(Invoice.class, id);
            if (managed == null) {
                throw new NotFoundException("Invoice with id " + id + " does not exist");
            }

            if (invoice != null) {
                if (invoice.getPaymentStatus() != null) {
                    managed.setPaymentStatus(invoice.getPaymentStatus());
                }
                if (invoice.getTotalAmount() != null) {
                    managed.setTotalAmount(invoice.getTotalAmount());
                }
                if (invoice.getDueDate() != null) {
                    managed.setDueDate(invoice.getDueDate());
                }
                if(invoice.getPayment() != null){
                    managed.setPayment(invoice.getPayment());
                }
            }

            transaction.commit();
        } catch (NotFoundException e) {
            throw e;
        }catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Error while updating invoice with id: " + id, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public void deleteInvoice(Long id) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Invoice invoice = session.find(Invoice.class, id);
            if (invoice == null) {
                throw new NotFoundException("Invoice with id " + id + " does not exist");
            }

            session.remove(invoice);
            transaction.commit();
        } catch (NotFoundException e) {
            throw e;
        }catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting invoice with id: " + id, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }
}
