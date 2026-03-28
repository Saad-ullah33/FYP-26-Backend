package com.propsightai.Repository;

import com.propsightai.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepositoy extends JpaRepository<Transaction,Integer> {
}
