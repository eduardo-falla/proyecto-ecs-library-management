package com.ecs.library.repository;

import com.ecs.library.model.Loan;
import com.ecs.library.model.LoanStatus;

import java.sql.Date;
import java.util.List;

public interface LoanRepository extends CrudRepository<Loan, Integer> {
    List<Loan> findByStudentId(int studentId);
    List<Loan> findActiveLoans();
    List<Loan> findOverdueLoans();
    boolean registerReturn(int loanId, Date returnDate, LoanStatus status);
    int countActiveLoansByStudent(int studentId);
}
