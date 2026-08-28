package com.ecs.library.model;

import java.sql.Date;

/**
 * Entidad Librarian que extiende de User (LSP).
 */
public class Librarian extends User {
    private int librarianId;
    private String staffId;
    private Gender gender;
    private String phone;
    private Date hiredDate;

    public Librarian() {
        super();
        this.role = UserRole.LIBRARIAN;
    }

    public Librarian(int userId, String username, String passwordHash, String salt,
                     String fullName, String email, UserStatus status,
                     int librarianId, String staffId, Gender gender, String phone, Date hiredDate) {
        super(userId, username, passwordHash, salt, fullName, email, UserRole.LIBRARIAN, status);
        this.librarianId = librarianId;
        this.staffId = staffId;
        this.gender = gender;
        this.phone = phone;
        this.hiredDate = hiredDate;
    }

    public int getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(int librarianId) {
        this.librarianId = librarianId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(Date hiredDate) {
        this.hiredDate = hiredDate;
    }
}
