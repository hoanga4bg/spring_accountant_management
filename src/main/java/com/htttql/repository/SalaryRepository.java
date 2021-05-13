package com.htttql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.htttql.entity.Salary;

public interface SalaryRepository extends JpaRepository<Salary, Integer>{

	public Salary findOneById(Integer id);

	public List<Salary> findByStatus(boolean b);

}
