package com.ecs.library.model;

import java.sql.Date;

/**
 * Entidad Student que extiende de User (LSP).
 */
public class Student extends User {
    private int studentId;
    private String regNo;
    private String department;
    private Date dateOfBirth;
    private Gender gender;
    private String contact;
    private Date dateJoined;

    public Student() {
        super();
        this.role = UserRole.STUDENT;
    }

    public Student(int userId, String username, String passwordHash, String salt,
                   String fullName, String email, UserStatus status,
                   int studentId, String regNo, String department, Date dateOfBirth,
                   Gender gender, String contact, Date dateJoined) {
        super(userId, username, passwordHash, salt, fullName, email, UserRole.STUDENT, status);
        this.studentId = studentId;
        this.regNo = regNo;
        this.department = department;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contact = contact;
        this.dateJoined = dateJoined;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }
}
