package com.dev.dao;

import com.dev.models.Privilege;
import com.dev.models.User;
import com.dev.models.UserPrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

@Component
public class UserPrivilegeJdbcDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PrivilegeJdbcDao privilegeJdbcDao;
    @Autowired
    public UserPrivilegeJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate, PrivilegeJdbcDao privilegeJdbcDao) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.privilegeJdbcDao = privilegeJdbcDao;
    }

    private final RowMapper<UserPrivilege> userRowMapper = (rs, rowNum) -> {
        UserPrivilege userPrivilege = new UserPrivilege();
        userPrivilege.setUserId(rs.getLong("UserId"));
        userPrivilege.setPrivilegeId(rs.getLong("PrivilegeId"));
        return userPrivilege;
    };

    private HashMap<String, Object> getUserPrivilegeMap(UserPrivilege userPrivilege) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("UserId", userPrivilege.getUserId());
        map.put("PrivilegeId", userPrivilege.getPrivilegeId());
        return map;
    }

    public void createUserPrivilege(UserPrivilege userPrivilege) {
        String sql = "INSERT INTO UserPrivileges (UserId, PrivilegeId) VALUES (:UserId,:PrivilegeId)";
        HashMap<String, Object> params = getUserPrivilegeMap(userPrivilege);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public Set<Privilege> getUserPrivilegesById(long id){
        String sql = "SELECT * FROM UserPrivileges WHERE UserId = :UserId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserId", id);
        List<UserPrivilege> userPrivileges = namedParameterJdbcTemplate.query(sql, params, userRowMapper);
        Set<Privilege> privileges = new java.util.HashSet<>();
        for (UserPrivilege userPrivilege : userPrivileges) {
            privileges.add(privilegeJdbcDao.getPrivilegeById(userPrivilege.getPrivilegeId()));
        }
        return privileges;
    }

    public void deleteUserPriviligesByUserId(long id) {
        String sql = "DELETE FROM UserPrivileges WHERE UserId = :UserId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserId", id);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
