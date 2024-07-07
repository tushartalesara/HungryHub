package com.dev;

// import com.dev.dao.RestaurantJdbcDao;
import com.dev.dao.UserJdbcDao;
// import com.dev.models.Item;
// import com.dev.models.Restaurant;
// import com.dev.models.User;
import com.dev.models.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
// import com.dev.dao.ItemJdbcDao;

@SpringBootApplication
public class DevApplication {

    public static void main(String[] args) {
        // for testing purposes
        var context = SpringApplication.run(DevApplication.class, args);
    }

}
