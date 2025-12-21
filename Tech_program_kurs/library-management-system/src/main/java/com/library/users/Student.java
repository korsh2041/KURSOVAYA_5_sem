package com.library.users;

public class Student extends User {
    private String studentId;
    private String department;

    public Student(String userId, String name, String email, String phone,
                   String studentId, String department) {
        super(userId, name, email, phone);
        this.studentId = studentId;
        this.department = department;
    }

    @Override
    public int getMaxBooksLimit() {
        return 5;
    }

    @Override
    protected boolean hasBorrowingRights() {
        return true;
    }

    @Override
    protected int calculateLoanPeriod() {
        return 30;
    }

    public String getStudentId() { return studentId; }
    public String getDepartment() { return department; }
}