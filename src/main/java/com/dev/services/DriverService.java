package com.dev.services;

import com.dev.dao.DriverJdbcDao;
import com.dev.dao.PincodeInfoJdbcDao;
import com.dev.models.Driver;
import com.dev.models.PincodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    DriverJdbcDao driverJdbcDao;
    PincodeInfoJdbcDao pincodeInfoJdbcDao;

    @Autowired
    public DriverService(DriverJdbcDao driverJdbcDao, PincodeInfoJdbcDao pincodeInfoJdbcDao) {
        this.driverJdbcDao = driverJdbcDao;
        this.pincodeInfoJdbcDao = pincodeInfoJdbcDao;
    }

    public Driver availableDriverByPincode(long pincode){
        return driverJdbcDao.getDriver(pincode);
    }

    public Driver availableDriverByCity(long pincode){
        return driverJdbcDao.getDriverByCity(pincode);
    }

    public void setDriverActive(long driverId){
        driverJdbcDao.setDriverStatus(driverId, 1);
    }

}
