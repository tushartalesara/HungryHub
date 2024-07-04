package com.dev.models;

import java.io.Serializable;

public class Privilege implements Serializable {
    long privilegeId;
    String privilegeName;

    public long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(long privilageId) {
        this.privilegeId = privilageId;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilageName) {
        this.privilegeName = privilageName;
    }

    @Override
    public String toString() {
        return "Privilege{" +
                "privilegeId=" + privilegeId +
                ", privilegeName='" + privilegeName + '\'' +
                '}';
    }
}
