package com.dev.dao;

import com.dev.models.Privilege;
import com.dev.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.HashMap;

@Component
public class UserJdbcDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private UserPrivilegeJdbcDao userPrivilegeJdbcDao;

    private PrivilegeJdbcDao privilegeJdbcDao;


    @Autowired
    public UserJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate, UserPrivilegeJdbcDao userPrivilegeJdbcDao, PrivilegeJdbcDao privilegeJdbcDao) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userPrivilegeJdbcDao = userPrivilegeJdbcDao;
        this.privilegeJdbcDao = privilegeJdbcDao;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setUserId(rs.getLong("UserId"));
        user.setFirstName(rs.getString("FirstName"));
        user.setLastName(rs.getString("LastName"));
        user.setPhoneNumber(rs.getLong("PhoneNumber"));
        user.setEmail(rs.getString("Email"));
        user.setUserPassword(rs.getString("UserPassword"));
        for (Privilege privilege : userPrivilegeJdbcDao.getUserPrivilegesById(user.getUserId())) {
            user.getPrivileges().add(privilege);
        }
        return user;
    };

    private HashMap<String, Object> getUserMap(User user) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("UserId", user.getUserId());
        map.put("FirstName", user.getFirstName());
        map.put("LastName", user.getLastName());
        map.put("PhoneNumber", user.getPhoneNumber());
        map.put("Email", user.getEmail());
        map.put("UserPassword", user.getUserPassword());
        return map;
    }

    public void createUser(User user) {
        if (user.getUserId()==0) user.setUserId(0);// sets it to a random val.
        String sql = "INSERT INTO Users (UserId, FirstName, LastName, PhoneNumber, Email, UserPassword) VALUES (:UserId,:FirstName, :LastName, :PhoneNumber, :Email, :UserPassword)";
        HashMap<String, Object> params = getUserMap(user);
        namedParameterJdbcTemplate.update(sql, params);
        if (user.getPrivileges() == null) {
            user.setPrivileges(new java.util.HashSet<>());
            user.getPrivileges().add(privilegeJdbcDao.getPrivilegeByName("ROLE_USER"));
        };
        for (Privilege privilege : user.getPrivileges()) {
            userPrivilegeJdbcDao.createUserPrivilege(new com.dev.models.UserPrivilege(user.getUserId(), privilege.getPrivilegeId()));
        }
    }

    public User queryForObject(String sql, HashMap<String, Object> params) {
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, userRowMapper);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public User getUserById(long id){
        String sql = "SELECT * FROM Users WHERE UserId = :UserId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserId", id);
        return queryForObject(sql, params);
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM Users";
        return namedParameterJdbcTemplate.query(sql, userRowMapper);
    }

    public User getUserByEmail(String email){
        String sql = "SELECT * FROM Users WHERE Email = :Email";
        HashMap<String, Object> params = new HashMap<>();
        params.put("Email", email);
        return queryForObject(sql, params);
    }
    @Cacheable(value = "user", key = "#user.email")
    public User getUserByUsername(String email){
        String sql = "SELECT * FROM Users WHERE Email = :Email";
        HashMap<String, Object> params = new HashMap<>();
        params.put("Email", email);
        return queryForObject(sql, params);
    }
    @CacheEvict(value = "user", key = "#user.email")
    public void updateUser(User user) {
        String sql = "UPDATE Users SET FirstName = :FirstName, LastName = :LastName, PhoneNumber = :PhoneNumber, Email = :Email, UserPassword = :UserPassword WHERE UserId = :UserId";
        userPrivilegeJdbcDao.deleteUserPriviligesByUserId(user.getUserId());
        for (Privilege privilege : user.getPrivileges()) {
            System.out.println(privilege);
            userPrivilegeJdbcDao.createUserPrivilege(new com.dev.models.UserPrivilege(user.getUserId(), privilege.getPrivilegeId()));
        }
        HashMap<String, Object> params = getUserMap(user);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
