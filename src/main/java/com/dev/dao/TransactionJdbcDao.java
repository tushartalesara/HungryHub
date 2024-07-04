package com.dev.dao;

import com.dev.models.Cart;
import com.dev.models.Transactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class TransactionJdbcDao {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public TransactionJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<Transactions> transactionsRowMapper = (rs, rowNum) -> {
        Transactions transactions = new Transactions();
        transactions.setCartId(rs.getLong("CartId"));
        transactions.setTransactionStatus(rs.getLong("TransactionStatus"));
        transactions.setTransactionType(rs.getString("TransactionType"));
        transactions.setTransactionTime(rs.getTimestamp("TransactionTime"));
        transactions.setTransactionId(rs.getString("TransactionId"));
        return transactions;
    };

    public HashMap<String, Object> getTransactionMap(Transactions transactions) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("CartId", transactions.getCartId());
        map.put("TransactionStatus", transactions.getTransactionStatus());
        map.put("TransactionType", transactions.getTransactionType());
        map.put("TransactionTime", transactions.getTransactionTime());
        map.put("TransactionId", transactions.getTransactionId());
        return map;
    }

    public void createTransaction(Transactions transactions) {
        String sql = "INSERT INTO Transactions (CartId, TransactionStatus, TransactionType, TransactionTime, TransactionId) VALUES (:CartId,:TransactionStatus, :TransactionType, :TransactionTime, :TransactionId)";
        HashMap<String, Object> params = getTransactionMap(transactions);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public Transactions getTransactionById(String id){
        String sql = "SELECT * FROM Transactions WHERE TransactionId = :TransactionId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("TransactionId", id);
        return namedParameterJdbcTemplate.queryForObject(sql, params, transactionsRowMapper);
    }

    public void updateTransaction(Transactions transactions) {
        String sql = "UPDATE Transactions SET TransactionStatus = :TransactionStatus, TransactionTime = :TransactionTime, CartId = :CartId, TransactionType = :TransactionType WHERE TransactionId = :TransactionId";
        HashMap<String, Object> params = getTransactionMap(transactions);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
