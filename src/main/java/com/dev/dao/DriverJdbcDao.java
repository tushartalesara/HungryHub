package com.dev.dao;

import com.dev.models.Driver;
import com.dev.models.PincodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class DriverJdbcDao {

    NamedParameterJdbcTemplate template;
    PincodeInfoJdbcDao pincodeInfoJdbcDao;

    @Autowired
    public DriverJdbcDao(NamedParameterJdbcTemplate template, PincodeInfoJdbcDao pincodeInfoJdbcDao) {
        this.template = template;
        this.pincodeInfoJdbcDao = pincodeInfoJdbcDao;
    }

    public HashMap<String, Object> getDriverMap(Driver driver){
        HashMap<String, Object> map = new HashMap<>();
        map.put("driverId", driver.getDriverId());
        map.put("userId", driver.getUserId());
        map.put("numberOfUsersRated", driver.getNumberOfUsersRated());
        map.put("rating", driver.getRating());
        map.put("pincode", driver.getPincode());
        map.put("status", driver.getStatus());
        map.put("imageUrl", driver.getImageUrl());
        return map;
    }

    private RowMapper<Driver> driverRowMapper = (rs, rowNum) -> {
        Driver driver = new Driver();
        driver.setDriverId(rs.getLong("driverId"));
        driver.setUserId(rs.getLong("userId"));
        driver.setNumberOfUsersRated(rs.getLong("numberOfUsersRated"));
        driver.setRating(rs.getDouble("rating"));
        driver.setPincode(rs.getLong("pincode"));
        driver.setStatus(rs.getInt("status"));
        driver.setImageUrl(rs.getString("imageUrl"));
        return driver;
    };

    public Driver queryForDriver(String sql, HashMap<String, Object> params){
        try {
            return template.query(sql, params, driverRowMapper).get(0);
        }
        catch (Exception e){
            return null;
        }
    }

    public void createDriver(Driver driver){
        String sql = "INSERT INTO drivers(driverId, userId, numberOfUsersRated, rating, pincode, status, imageUrl) VALUES (:driverId, :userId, :numberOfUsersRated, :rating, :pincode, :status, :imageUrl)";
        HashMap<String, Object> params = getDriverMap(driver);
        template.update(sql, params);
    }

    public Driver getDriverById(long id){
        String sql = "SELECT * FROM drivers WHERE driverId = :driverId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("driverId", id);
        return queryForDriver(sql, params);
    }

    public Driver getDriver(long pincode){
        String sql = "SELECT * FROM drivers WHERE pincode = :pincode AND status = 0";
        HashMap<String, Object> params = new HashMap<>();
        params.put("pincode", pincode);
        return queryForDriver(sql, params);
    }

    public Driver getDriverByCity(long pincode){
        PincodeInfo pincodeInfo = pincodeInfoJdbcDao.getPincodeInfo(pincode);
        if (pincodeInfo == null) return null;
        String sql = "SELECT * FROM drivers WHERE pincode IN (SELECT pincode FROM pincodeInfo WHERE city = :city AND state = :state) AND status = 0";
        HashMap<String, Object> params = new HashMap<>();
        params.put("city", pincodeInfo.getPincode());
        params.put("state", pincodeInfo.getState());
        return queryForDriver(sql, params);
    }

    public void setDriverStatus(long driver_id, int status){
        String sql = "UPDATE drivers SET status = :status WHERE driverId = :driverId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("driverId", driver_id);
        params.put("status", status);
        template.update(sql, params);
    }

    public void updateDriver(Driver driver){
        try{
            String sql = "UPDATE drivers SET userId = :userId, numberOfUsersRated = :numberOfUsersRated, rating = :rating, pincode = :pincode, status = :status, imageUrl = :imageUrl WHERE driverId = :driverId";
            HashMap<String, Object> params = getDriverMap(driver);
            template.update(sql, params);
        }
        catch (Exception e){
        }
    }

    public Driver getDriverByUserId(long userId){
        String sql = "SELECT * FROM drivers WHERE userId = :userId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return queryForDriver(sql, params);
    }

    public void updateDriverPincode(long userId,long pincode){
        String sql = "UPDATE drivers SET pincode = :pincode,status = 0 WHERE userId = :userId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pincode",pincode);
        template.update(sql, params);
    }

    public void updateDriverStatus(int status,long userId){
        String sql = "UPDATE drivers SET status = :status WHERE userId = :userId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("status",status);
        params.put("userId",userId);
        template.update(sql, params);
    }
}
