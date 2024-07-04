package com.dev.dao;

import com.dev.models.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class UserAddressJdbcDao {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserAddressJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private RowMapper<UserAddress> rowMapper = (rs, rowNum) -> {
        UserAddress userAddress = new UserAddress();
        userAddress.setAddressId(rs.getLong("AddressId"));
        userAddress.setStreetAddress(rs.getString("StreetAddress"));
        userAddress.setPincode(rs.getLong("Pincode"));
        userAddress.setUserId(rs.getLong("UserId"));
        return userAddress;
    };

    private HashMap<String, Object> getUserAddressMap(UserAddress userAddress) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("AddressId", userAddress.getAddressId());
        map.put("StreetAddress", userAddress.getStreetAddress());
        map.put("Pincode", userAddress.getPincode());
        map.put("UserId", userAddress.getUserId());
        return map;
    }

    public List<UserAddress> getUserAddressesByUserIdAndCity(long userId, String city, String state) {
        String sql = "SELECT * FROM UserAddress WHERE UserId=:userId AND Pincode IN (SELECT Pincode FROM pincodeInfo WHERE city=:city AND state=:state)";
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("city", city);
        params.put("state", state);
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    public UserAddress queryForUserAddress(String sql, HashMap<String, Object> params){
        try {
            return namedParameterJdbcTemplate.query(sql, params, rowMapper).get(0);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public UserAddress getUserAddressById(long addressId) {
        String sql = "SELECT * FROM UserAddress WHERE AddressId=:addressId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("addressId", addressId);
        return queryForUserAddress(sql, params);
    }

    public void createUserAddress(UserAddress userAddress) {
        String sql = "INSERT INTO UserAddress (AddressId, StreetAddress, Pincode, UserId) VALUES (:addressId, :streetAddress, :pincode, :userId)";
        HashMap<String, Object> params = new HashMap<>();
        params.put("addressId", userAddress.getAddressId());
        params.put("streetAddress", userAddress.getStreetAddress());
        params.put("pincode", userAddress.getPincode());
        params.put("userId", userAddress.getUserId());
        namedParameterJdbcTemplate.update(sql, params);
    }

    public void deleteUserAddress(long addressId) {
        String sql = "DELETE FROM userAddress WHERE AddressId = :addressId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("addressId", addressId);

        //  Map<String, Long> paramMap = Collections.singletonMap("addressId", addressId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public List<UserAddress> getUserAddressesByUserId(long userId) {
        String sql = "SELECT * FROM userAddress WHERE UserId = :userId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }
    public void updateUserAddress(UserAddress userAddress) {
        String sql = "UPDATE userAddress SET StreetAddress = :StreetAddress, Pincode = :Pincode " +
                "WHERE AddressId = :AddressId";
        HashMap<String, Object> params = getUserAddressMap(userAddress);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
