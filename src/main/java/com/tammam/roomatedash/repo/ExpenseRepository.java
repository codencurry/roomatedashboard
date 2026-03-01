package com.tammam.roomatedash.repo;

import com.tammam.roomatedash.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {}
