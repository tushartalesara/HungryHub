package com.dev.dao;

import com.dev.models.Cart;
import com.dev.models.PincodeInfo;
import okhttp3.CertificatePinner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;

import java.util.HashMap;

@Repository
public class PincodeInfoJdbcDao {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public PincodeInfoJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<PincodeInfo> pincodeInfoRowMapper = (rs, rowNum) -> {
        PincodeInfo pincodeInfo = new PincodeInfo();
        pincodeInfo.setPincode(rs.getLong("pincode"));
        pincodeInfo.setCity(rs.getString("city"));
        pincodeInfo.setState(rs.getString("state"));
        return pincodeInfo;
    };

    public HashMap<String, Object> getPincodeMap(PincodeInfo pincodeInfo){
        HashMap<String, Object> map = new HashMap<>();
        map.put("pincode", pincodeInfo.getPincode());
        map.put("city", pincodeInfo.getCity());
        map.put("state", pincodeInfo.getState());
        return map;
    }

    public PincodeInfo queryForPincodeInfo(String sql, HashMap<String, Object> params){
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, pincodeInfoRowMapper);
        }
        catch (Exception e){
            return null;
        }
    }
    @Cacheable(value = "pincodeInfo", key= "#pincode")
    public PincodeInfo getPincodeInfo(long pincode){
        String sql = "SELECT * FROM pincodeInfo WHERE pincode=:pincode";
        HashMap<String, Object> params = new HashMap<>();
        params.put("pincode", pincode);
        return queryForPincodeInfo(sql, params);
    }

    @Cacheable(value = "pincodeInfos", key = "#city + #state")
    public List<PincodeInfo> getPincodeInfosByCity(String city, String state){
        String sql = "SELECT * FROM pincodeInfo WHERE city=:city AND state=:state";
        HashMap<String, Object> params = new HashMap<>();
        params.put("city", city);
        params.put("state", state);
        return namedParameterJdbcTemplate.query(sql, params, pincodeInfoRowMapper);
    }
}
