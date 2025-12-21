package com.library.users;

import com.library.notifications.NotificationObserver;
import java.util.Date;

public abstract class User implements NotificationObserver {
    protected String userId;
    protected String name;
    protected String email;
    protected String phone;
    protected Date registrationDate;

    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.registrationDate = new Date();
    }

    // Template Method
    public final boolean canBorrowBook(int currentLoans) {
        return currentLoans < getMaxBooksLimit() && hasBorrowingRights();
    }

    public final int getLoanPeriod() {
        return calculateLoanPeriod();
    }

    // Абстрактные методы для переопределения
    public abstract int getMaxBooksLimit();
    protected abstract boolean hasBorrowingRights();
    protected abstract int calculateLoanPeriod();

    // Observer implementation
    @Override
    public void update(String message) {
        System.out.println("Уведомление для " + name + ": " + message);
    }

    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Date getRegistrationDate() { return registrationDate; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}