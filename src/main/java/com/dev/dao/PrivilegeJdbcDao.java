package com.dev.dao;

import com.dev.models.Privilege;
import com.dev.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.HashMap;

@Component
public class PrivilegeJdbcDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    public PrivilegeJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<Privilege> userRowMapper = (rs, rowNum) -> {
        Privilege privilege = new Privilege();
        privilege.setPrivilegeId(rs.getLong("Id"));
        privilege.setPrivilegeName(rs.getString("Privilege"));
        return privilege;
    };

    private HashMap<String, Object> getPrivilegeMap(Privilege privilege) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("PrivilegeId", privilege.getPrivilegeId());
        map.put("PrivilegeName", privilege.getPrivilegeName());
        return map;
    }

    public void createPrivilege(Privilege privilege) {
        String sql = "INSERT INTO Privileges (Id, privilege) VALUES (:PrivilegeId,:PrivilegeName)";
        HashMap<String, Object> params = getPrivilegeMap(privilege);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public Privilege getPrivilegeById(long id){
        String sql = "SELECT * FROM Privileges WHERE Id = :PrivilegeId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("PrivilegeId", id);
        return namedParameterJdbcTemplate.queryForObject(sql, params, userRowMapper);
    }

    public Privilege getPrivilegeByName(String name) {
        String sql = "SELECT * FROM Privileges WHERE privilege = :PrivilegeName";
        HashMap<String, Object> params = new HashMap<>();
        params.put("PrivilegeName", name);
        return namedParameterJdbcTemplate.queryForObject(sql, params, userRowMapper);
    }
}
