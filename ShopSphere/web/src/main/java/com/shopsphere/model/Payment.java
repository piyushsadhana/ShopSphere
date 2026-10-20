package com.shopsphere.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Represents a payment transaction record.
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING, SUCCESS, FAILED
    }

    private int paymentId;
    private int orderId;
    private BigDecimal amount;
    private String method;
    private Status status;
    private String transactionRef;
    private Timestamp paymentDate;

    public Payment() {
        this.amount = BigDecimal.ZERO;
        this.status = Status.PENDING;
    }

    public Payment(int paymentId, int orderId, BigDecimal amount, String method, Status status, String transactionRef) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.method = method;
        this.status = status != null ? status : Status.PENDING;
        this.transactionRef = transactionRef;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }
}
