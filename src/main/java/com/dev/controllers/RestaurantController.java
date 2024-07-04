package com.dev.controllers;

import com.dev.services.AuthenticatedUser;
import com.dev.services.CloudinaryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dev.dao.*;
import com.dev.models.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;
import java.util.*;
import java.io.IOException;

@RequestMapping("/restaurants")
@Controller
public class RestaurantController {
    private final ItemJdbcDao itemJdbcDao;
    private final RestaurantJdbcDao restaurantJdbcDao;

    private final AuthenticatedUser authenticatedUser;

    private final CartJdbcDao cartJdbcDao;

    private final CartItemsJdbcDao cartItemsJdbcDao;
    private final CloudinaryImageService cloudinaryImageService;
    private final PrivilegeJdbcDao privilegeJdbcDao;
    private final UserJdbcDao userJdbcDao;
    private final OrderJdbcDao orderJdbcDao;

    @Autowired
    public RestaurantController(ItemJdbcDao itemJdbcDao, RestaurantJdbcDao restaurantJdbcDao, AuthenticatedUser authenticatedUser, CartJdbcDao cartJdbcDao, CartItemsJdbcDao cartItemsJdbcDao, CloudinaryImageService cloudinaryImageService, PrivilegeJdbcDao privilegeJdbcDao, UserJdbcDao userJdbcDao, OrderJdbcDao orderJdbcDao) {
        this.itemJdbcDao = itemJdbcDao;
        this.restaurantJdbcDao = restaurantJdbcDao;
        this.authenticatedUser = authenticatedUser;
        this.cartJdbcDao = cartJdbcDao;
        this.cartItemsJdbcDao = cartItemsJdbcDao;
        this.cloudinaryImageService = cloudinaryImageService;
        this.privilegeJdbcDao = privilegeJdbcDao;
        this.userJdbcDao = userJdbcDao;
        this.orderJdbcDao = orderJdbcDao;
    }
    
    @GetMapping("")
    public String getAllRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantJdbcDao.getAllRestaurants());
        model.addAttribute("itemSearch",false);
        return "restaurants";
    }
    
    @CrossOrigin
    @RequestMapping("/{id}")
    public String getAllItems(@PathVariable("id") long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var items = itemJdbcDao.getAllItemsbyRestaurantId(id);
        var restaurant = restaurantJdbcDao.getRestaurantById(id);
        model.addAttribute("items", items);
        model.addAttribute("restaurant", restaurant);
        Cart currentCart = null;
        User user = null;
        try {
            user = authenticatedUser.getAuthenticatedUser(userDetails);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("User not signed in");// user is not signed in
        }
        if (user==null)
            currentCart = new Cart();
        else {
            try{
                currentCart = cartJdbcDao.getCurrentCart(user.getUserId());
            } catch (Exception e) {
                System.out.println(e.getMessage()); // user has no cart
                System.out.println("User has no cart");
            }
            if (currentCart==null)
                currentCart = new Cart();
        }
        model.addAttribute("cart", currentCart);
        List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(currentCart.getCartId());
        Map<Long, String> cartItemsMap = new HashMap<>();
        for (CartItems cartItem : cartItems) {
            cartItemsMap.put(cartItem.getItemId(), cartItem.getQuantity()+"");
        }
        for (Item item : items){
            if (!cartItemsMap.containsKey(item.getItemId())) cartItemsMap.put(item.getItemId(), "0");
        }
        model.addAttribute("cartItemsMap", cartItemsMap);
        return "items";
    }

    @CrossOrigin
    @GetMapping("/{id}/add")
    public String addItem(@PathVariable("id") long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        model.addAttribute("rid", id+"");
        Restaurant r = null;
        try{
            r = restaurantJdbcDao.getRestaurantById(id);
        }
        catch (Exception e){
            System.out.println(e);
        }
        if (r==null||r.getUserId()!=user.getUserId())
            return "redirect:/restaurants";
        model.addAttribute("item", new Item());
        return "addItem";
    }

    @CrossOrigin
    @PostMapping("/{id}/add")
    public String addItem(@PathVariable("id") long id, Item item, @AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        var restaurant = restaurantJdbcDao.getRestaurantById(id);
        if (restaurant==null||restaurant.getUserId()!=user.getUserId())
            return "redirect:/restaurants";
        if (restaurant.getIsVegeterian() && !item.getIsVegeterian()) {
            return "redirect:/restaurants/" + id + "/add";
        }
        item.setRestaurantId(id);
        itemJdbcDao.createItem(item);
        return "redirect:/restaurants/"+id;
    }

    @GetMapping("/create")
    public String createRestaurant(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "create_restaurant" ;
    }


    @PostMapping("/create")
    public String createRestaurant(Restaurant restaurant, @AuthenticationPrincipal UserDetails userDetails, @RequestParam("startT") String startT, @RequestParam("endT") String endT, @RequestParam("image") MultipartFile imageFile){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        restaurant.setStartTime(new Time(Integer.parseInt(startT.substring(0,2)),Integer.parseInt(startT.substring(3,5)),0));
        restaurant.setEndTime(new Time(Integer.parseInt(endT.substring(0,2)),Integer.parseInt(endT.substring(3,5)),0));
        restaurant.setUserId(user.getUserId());
        Map fileName = cloudinaryImageService.upload(imageFile);
        String secureUrl = (String) fileName.get("secure_url");
        restaurant.setImageUrl(secureUrl);
        System.out.println(fileName);
        boolean check = true;
        for (Privilege p: user.getPrivileges()) {
            if (p.getPrivilegeName().equals("ROLE_RESTAURANT")) {
                check = false;
                break;
            }
        }
        if (check) {
            user.getPrivileges().add(privilegeJdbcDao.getPrivilegeByName("ROLE_RESTAURANT"));
            userJdbcDao.updateUser(user);
        }
        try {
            restaurantJdbcDao.createRestaurant(restaurant);
            return "redirect:/restaurants";
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/restaurants/create" ;
        }
    }

//    @RequestMapping("/my")
//    public String myRestaurants(@AuthenticationPrincipal UserDetails userDetails, Model model){
//        User user = authenticatedUser.getAuthenticatedUser(userDetails);
//        model.addAttribute("restaurants", restaurantJdbcDao.getRestaurantByUserId(user.getUserId()));
//        return "restaurants";
//    }
    @CrossOrigin
    @GetMapping("/my")
    public String getRestaurantsByUser( Model model, @AuthenticationPrincipal UserDetails userDetails) {

        User user = authenticatedUser.getAuthenticatedUser(userDetails);

        List<Restaurant> restaurants = restaurantJdbcDao.getRestaurantByUserId(user.getUserId());

        model.addAttribute("restaurants", restaurants);



        return "userrestaurants";
    }

    @CrossOrigin
    @RequestMapping("manage/{id}")
    public String getAllItemsFormanage(@PathVariable("id") long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var items = itemJdbcDao.getAllItemsbyRestaurantId(id);
        model.addAttribute("items", items);
        model.addAttribute("restaurantId", id);

        List<Cart> carts = cartJdbcDao.getCartsByRestaurantId(id);
        Map<Order, Map<Item, Long>> orderCartItemsMap = new HashMap<>();
        for (Cart cart : carts) {
            Order order = orderJdbcDao.getOrderByCartId(cart.getCartId());
            List<CartItems> cartitems = cartItemsJdbcDao.getCartItemsByCartId(cart.getCartId());
            Map<Item, Long> cartItemsQuantityMap = new HashMap<>();
            for (CartItems cartItem : cartitems) {
                Item item = itemJdbcDao.getItemById(cartItem.getItemId());

                cartItemsQuantityMap.put(item, cartItem.getQuantity());
            }
            orderCartItemsMap.put(order, cartItemsQuantityMap);
        }
        model.addAttribute("orders",orderCartItemsMap);
        Cart currentCart = null;
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        if (user==null)
            currentCart = new Cart();
        else {
            currentCart = cartJdbcDao.getCurrentCart(user.getUserId());
            if (currentCart==null)
                currentCart = new Cart();
        }
        model.addAttribute("cart", currentCart);
        List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(currentCart.getCartId());
        Map<Long, String> cartItemsMap = new HashMap<>();
        for (CartItems cartItem : cartItems) {
            cartItemsMap.put(cartItem.getItemId(), cartItem.getQuantity()+"");
        }
        for (Item item : items){
            if (!cartItemsMap.containsKey(item.getItemId())) cartItemsMap.put(item.getItemId(), "0");
        }
        model.addAttribute("cartItemsMap", cartItemsMap);
        return "updaterestauranritems";
    }

    @GetMapping("/searchRestaurant/{query}")
    public String searchRestaurant(@PathVariable("query") String query){
        Long id = restaurantJdbcDao.getRestaurantWithName(query).getRestaurantId();
        return "redirect:/restaurants/"+ id;
    }

    @GetMapping("/searchItem/{query}")
    public String searchItem(@PathVariable("query") String query,Model model){
        List<Item> items = itemJdbcDao.getItemsWithNameLike(query);
        List<Restaurant> restaurants = new ArrayList<>();
        for(Item item:items){
            Restaurant restaurant = restaurantJdbcDao.getRestaurantById(item.getRestaurantId());
            restaurants.add(restaurant);
        }
        model.addAttribute("restaurants",restaurants);
        model.addAttribute("itemSearch",true);
        model.addAttribute("item",query);
        return "restaurants";
    }

    @CrossOrigin
    @RequestMapping("/{id}/{query}")
    public String getAllParticularItems(@PathVariable("id") long id,@PathVariable("query")String query, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var items = itemJdbcDao.getAllItemsbyRestaurantIdAndName(id,query);
        var restaurant = restaurantJdbcDao.getRestaurantById(id);
        model.addAttribute("items", items);
        model.addAttribute("restaurant", restaurant);
        Cart currentCart = null;
        User user = null;
        try {
            user = authenticatedUser.getAuthenticatedUser(userDetails);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("User not signed in");// user is not signed in
        }
        if (user==null)
            currentCart = new Cart();
        else {
            try{
                currentCart = cartJdbcDao.getCurrentCart(user.getUserId());
            } catch (Exception e) {
                System.out.println(e.getMessage()); // user has no cart
                System.out.println("User has no cart");
            }
            if (currentCart==null)
                currentCart = new Cart();
        }
        model.addAttribute("cart", currentCart);
        List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(currentCart.getCartId());
        Map<Long, String> cartItemsMap = new HashMap<>();
        for (CartItems cartItem : cartItems) {
            cartItemsMap.put(cartItem.getItemId(), cartItem.getQuantity()+"");
        }
        for (Item item : items){
            if (!cartItemsMap.containsKey(item.getItemId())) cartItemsMap.put(item.getItemId(), "0");
        }
        model.addAttribute("cartItemsMap", cartItemsMap);
        return "items";
    }

    @CrossOrigin
    @PostMapping("manage/{id}/add")
    public String addItemTORestanurant(@PathVariable("id") long id, Item item, @AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        var restaurant = restaurantJdbcDao.getRestaurantById(id);
        if (restaurant==null||restaurant.getUserId()!=user.getUserId())
            return "redirect:/restaurants";
        if (restaurant.getIsVegeterian() && !item.getIsVegeterian()) {
            return "redirect:/restaurants/" + id + "/add";
        }
        item.setRestaurantId(id);
        itemJdbcDao.createItem(item);
        return "redirect:/restaurants/manage/"+id;
    }

    @CrossOrigin
    @PostMapping("manage/create")
    public String createRestaurantForUser(Restaurant restaurant, Model model, @AuthenticationPrincipal UserDetails userDetails
            ,@RequestParam("image") MultipartFile imageFile) throws IOException, InterruptedException {

        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        restaurant.setUserId(user.getUserId());

        Map fileName = cloudinaryImageService.upload(imageFile);
        String secureUrl = (String) fileName.get("secure_url");
        // Now you can use the secureUrl as needed
        restaurant.setImageUrl(secureUrl);
        //  restaurant.setImageUrl(fileName.secure_url);
        System.out.println(fileName);
        restaurantJdbcDao.createRestaurant(restaurant);
        // return "redirect:/restaurants/users/" + user.getUserId();
        return "redirect:/restaurants/my";
    }

    @GetMapping("manage/updateItem/{itemId}")
    public String updateItemAvailability(@PathVariable("itemId") long itemId, Model model) {
        Item item = itemJdbcDao.getItemById(itemId);

        // Toggle the item's availability status
        item.setAvailable(!item.isAvailable());

        // Update the item in the database
        itemJdbcDao.updateItem(item);

        // Redirect back to the restaurant's item list with the restaurant ID
        // This assumes you have a method for fetching the restaurant's items and adding them to the model
        long restaurantId = item.getRestaurantId();
        List<Item> items = itemJdbcDao.getAllItemsbyRestaurantId(restaurantId);
        model.addAttribute("items", items);

        return "redirect:/restaurants/manage/" + restaurantId;
    }

    @PostMapping("manage/addItem/{restaurantId}")
    public String addItemToRestaurant (@PathVariable("restaurantId") long restaurantId,Item item,Model model,@RequestParam("isVegetarian") boolean isVegetarian,
                                       @RequestParam("image") MultipartFile imageFile) {


        // Toggle the item's availability status
        item.setIsVegeterian(isVegetarian);
        item.setRestaurantId(restaurantId);
        Map fileName = cloudinaryImageService.upload(imageFile);
        String secureUrl = (String) fileName.get("secure_url");
        // Now you can use the secureUrl as neededset
        item.setImageUrl(secureUrl);
        // Update the item in the database
        itemJdbcDao.createItem(item);

        // Redirect back to the restaurant's item list with the restaurant ID
        // This assumes you have a method for fetching the restaurant's items and adding them to the model
        // long restaurant = item.getRestaurantId();
        List<Item> items = itemJdbcDao.getAllItemsbyRestaurantId(restaurantId);
        model.addAttribute("items", items);
        model.addAttribute("restaurantId", restaurantId);

        return "redirect:/restaurants/manage/" + restaurantId;
    }

}














