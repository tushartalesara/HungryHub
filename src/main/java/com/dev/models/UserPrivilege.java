package com.dev.models;

public class UserPrivilege {
    long userId;
    long privilegeId;

    public UserPrivilege(long userId, long privilegeId) {
        this.userId = userId;
        this.privilegeId = privilegeId;
    }

    public UserPrivilege() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(long privilegeId) {
        this.privilegeId = privilegeId;
    }

    @Override
    public String toString() {
        return "UserPrivilege{" +
                "userId=" + userId +
                ", privilegeId=" + privilegeId +
                '}';
    }
}
