package com.dev.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.dev.models.Promocode;

@Component
public class PromocodeJdbcDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public PromocodeJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<Promocode> promocodeRowMapper= (rs, rowNum) ->{
        Promocode promocode = new Promocode();
        promocode.setPromocodeId(rs.getLong("PromocodeId"));
        promocode.setPromoName(rs.getString("PromoName"));
        promocode.setStartDate(rs.getDate("StartDate"));
        promocode.setEndDate(rs.getDate("EndDate"));
        promocode.setDiscount(rs.getLong("Discount"));
        promocode.setMinimumOrderValue(rs.getLong("MinimumOrderValue"));
        promocode.setPromoDescription(rs.getString("PromoDescription"));
        return promocode;
    };

    private HashMap<String, Object> getPromocodeMap(Promocode promocode){
        HashMap<String, Object> map = new HashMap<>();
        map.put("PromocodeId", promocode.getPromocodeId());
        map.put("PromoName", promocode.getPromoName());
        map.put("StartDate", promocode.getStartDate());
        map.put("EndDate", promocode.getEndDate());
        map.put("Discount", promocode.getDiscount());
        map.put("MinimumOrderValue", promocode.getMinimumOrderValue());
        map.put("PromoDescription", promocode.getPromoDescription());
        return map;
    }

    public void createPromocode(Promocode promocode) {
        String sql = "INSERT INTO Promocode (PromocodeId, PromoName, StartDate, EndDate, Discount, MinimumOrderValue, PromoDescription) VALUES (:PromocodeId, :PromoName, :StartDate, :EndDate, :Discount, :MinimumOrderValue, :PromoDescription)";
        HashMap<String, Object> params = getPromocodeMap(promocode);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public List<Promocode> getAllPromocodes() {
        String sql = "SELECT * FROM Promocode";
        return namedParameterJdbcTemplate.query(sql, promocodeRowMapper);
    }
    public Promocode queryForPromocode(String sql, HashMap<String, Object> params){
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, promocodeRowMapper);
        }
        catch (Exception e){
            return null;
        }
    }

    public Promocode getPromocodeByPromocodeName(String promoName){
        String sql = "SELECT * FROM Promocode WHERE PromoName = :PromoName";
        HashMap<String, Object> params = new HashMap<>();
        params.put("PromoName", promoName);
        return queryForPromocode(sql, params);
    }

    public Promocode getPromocodeByPromocodeId(long promocodeId){
        String sql = "SELECT * FROM Promocode WHERE PromocodeId = :PromocodeId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("PromocodeId", promocodeId);
        return queryForPromocode(sql, params);
    }
}
