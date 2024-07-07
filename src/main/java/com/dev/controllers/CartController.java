package com.dev.controllers;
 
import com.dev.dao.*;
import com.dev.models.*;
import com.dev.services.AuthenticatedUser;
import com.dev.services.OrderService;
import com.dev.services.PaymentService;
import com.dev.utilities.PaymentObject;
import com.dev.utilities.Total;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;

@RequestMapping("/cart")
@Controller
public class CartController {
    private CartJdbcDao cartJdbcDao;
    private CartItemsJdbcDao cartItemsJdbcDao;
    private ItemJdbcDao itemJdbcDao;
    private AuthenticatedUser authenticatedUser;
    private RestaurantJdbcDao restaurantJdbcDao;
    private PromocodeJdbcDao promocodeJdbcDao;
    private PaymentService paymentService;
    private OrderService orderService;
    private TransactionJdbcDao transactionJdbcDao;
    private PincodeInfoJdbcDao pincodeInfoJdbcDao;
    private UserAddressJdbcDao userAddressJdbcDao;

    @Value("${RAZORPAY_PUBLIC_KEY}")
    private String publicKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Autowired
    public CartController(CartJdbcDao cartJdbcDao, CartItemsJdbcDao cartItemsJdbcDao, ItemJdbcDao itemJdbcDao, AuthenticatedUser authenticatedUser, RestaurantJdbcDao restaurantJdbcDao, PromocodeJdbcDao promocodeJdbcDao, PaymentService paymentService, OrderService orderService, TransactionJdbcDao transactionJdbcDao, PincodeInfoJdbcDao pincodeInfoJdbcDao, UserAddressJdbcDao userAddressJdbcDao) {
        this.cartJdbcDao = cartJdbcDao;
        this.cartItemsJdbcDao = cartItemsJdbcDao;
        this.itemJdbcDao = itemJdbcDao;
        this.authenticatedUser = authenticatedUser;
        this.restaurantJdbcDao = restaurantJdbcDao;
        this.promocodeJdbcDao = promocodeJdbcDao;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.transactionJdbcDao = transactionJdbcDao;
        this.pincodeInfoJdbcDao = pincodeInfoJdbcDao;
        this.userAddressJdbcDao = userAddressJdbcDao;
    }

    // -- Rohith |
    // Test with postman
    @GetMapping("/add/{id}/{quantity}") // js sends this request when user presses add to cart button. User doesn't interact with this api.
    @ResponseBody
    public String addItem(@PathVariable("id") long id, @PathVariable("quantity") long q, @AuthenticationPrincipal UserDetails userDetails){ // think of conventions of what to send on success or on failure
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        if (user==null){// cant add. user not logged in. Actually should be redirected to login page.
            return "redirect:/login";
        }
        Item item = itemJdbcDao.getItemById(id);
        if (item==null){
            return "item not found!";
        }
        Cart cart = cartJdbcDao.getCurrentCart(user.getUserId());
        if (cart==null){
            cart = new Cart(user.getUserId(), false, item.getRestaurantId());
            cartJdbcDao.createCart(cart);
            cartItemsJdbcDao.createCartItems(new CartItems(cart.getCartId(), item.getItemId(), q));
            return "New cart created.";
        }
        else if (cart.getRestaurantId()!=item.getRestaurantId()){
            // Think about what to do here. Should we create a new cart or should we add to the current cart? -- Taking a new cart for now.
            cart.setStatus(true);
            cartJdbcDao.updateCart(cart);
            cartJdbcDao.deleteCart(cart);
            cart = new Cart(user.getUserId(), false, item.getRestaurantId());
            cartJdbcDao.createCart(cart);
            cartItemsJdbcDao.createCartItems(new CartItems(cart.getCartId(), item.getItemId(), q));
            return "New cart created.";
        }
            // check whether the item is already in the cart. If yes, update the quantity. If no, add the item to the cart.
        CartItems cartItems = cartItemsJdbcDao.getCartItemsByCartIdAndItemId(cart.getCartId(), item.getItemId());
        if (cartItems==null){
            cartItemsJdbcDao.createCartItems(new CartItems(cart.getCartId(), item.getItemId(), q));
            return "Item added to cart.";
        }
        cartItems.setQuantity(q);
        if (q<=0)
            cartItemsJdbcDao.deleteCartItems(cartItems);
        else
            cartItemsJdbcDao.updateCartItems(cartItems);
        List<CartItems> allCartItems = cartItemsJdbcDao.getCartItemsByCartId(cart.getCartId());
        if (allCartItems.isEmpty()){
            cart.setStatus(true);
            cartJdbcDao.updateCart(cart);
        }
        return ""+item.getPrice();
    }
    @GetMapping("/total")
    @ResponseBody
    @CrossOrigin
    public Total getCartTotal(@AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        Cart cart = cartJdbcDao.getCurrentCart(user.getUserId());
        if (cart==null) return new Total();
        List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(cart.getCartId());
        long totalPrice = 0, discount = 0, finalPrice = 0;
        for (CartItems cartItem : cartItems) {
            totalPrice += cartItem.getQuantity() * itemJdbcDao.getItemById(cartItem.getItemId()).getPrice();
        }
        int valid = 1;
        finalPrice = totalPrice;
        if (cart.getPromocodeId()!=0){
            Promocode promocode = promocodeJdbcDao.getPromocodeByPromocodeId(cart.getPromocodeId());
            if (promocode!=null){
                if (totalPrice<promocode.getMinimumOrderValue()){
                    valid = 0;
                    cart.setPromocodeId(0);
                } else
                    finalPrice = max(totalPrice-promocode.getDiscount()*totalPrice/100, promocode.getMinimumOrderValue());
            }
        }
        else valid = 0;
        discount = totalPrice - finalPrice;
        return new Total(totalPrice, discount, finalPrice, valid);
    }
    @GetMapping("/try/")
    @ResponseBody
    @CrossOrigin
    public void nonePromocode(@AuthenticationPrincipal UserDetails userDetails){
        tryPromocode(null, userDetails);
    }

    @GetMapping("/try/{promocode}")
    @ResponseBody
    @CrossOrigin
    public void tryPromocode(@Nullable @PathVariable("promocode") String promocode, @AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        Cart cart = cartJdbcDao.getCurrentCart(user.getUserId());
        if (cart==null){
            return;
        }
        Promocode promocode1 = promocodeJdbcDao.getPromocodeByPromocodeName(promocode);
        if (promocode1==null){
            cart.setPromocodeId(0);
        }
        else cart.setPromocodeId(promocode1.getPromocodeId());
        cartJdbcDao.updateCart(cart);
    }

    @GetMapping("/view")
    public String viewCart(Model model, @AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        if (user==null){
            return "redirect:/login";
        }
        Cart cart = cartJdbcDao.getCurrentCart(user.getUserId());
        if (cart==null)
            model.addAttribute("present", false);
        else {
            model.addAttribute("cart", cart);
            var restaurant = restaurantJdbcDao.getRestaurantById(cart.getRestaurantId());
            model.addAttribute("restaurant", restaurant);
            List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(cart.getCartId());
            if (cartItems.isEmpty()) {
                model.addAttribute("present", false);
            }else {
                model.addAttribute("cartItems", cartItems);
                Map<Long, Item> items = new HashMap<>();
                for (CartItems cartItem : cartItems) items.put(cartItem.getItemId(), itemJdbcDao.getItemById(cartItem.getItemId()));
                model.addAttribute("present", true);
                model.addAttribute("items", items);
                model.addAttribute("total", getCartTotal(userDetails));
                Promocode promocode = promocodeJdbcDao.getPromocodeByPromocodeId(cart.getPromocodeId());
                model.addAttribute("publicKey", publicKey);
                var pincodeInfo = pincodeInfoJdbcDao.getPincodeInfo(restaurant.getPincode());
                List<PincodeInfo> pincodeInfos = pincodeInfoJdbcDao.getPincodeInfosByCity(pincodeInfo.getCity(), pincodeInfo.getState());
                model.addAttribute("pincodeInfos", pincodeInfos);
                List<UserAddress> userAddresses = userAddressJdbcDao.getUserAddressesByUserIdAndCity(user.getUserId(), pincodeInfo.getCity(), pincodeInfo.getState());
                model.addAttribute("userAddresses", userAddresses);
                model.addAttribute("cityname", pincodeInfo.getCity());
                model.addAttribute("statename", pincodeInfo.getState());
                if (promocode!=null) model.addAttribute("promocode", promocode.getPromoName());
                else model.addAttribute("promocode");
            }
        }
        return "site/cart";
    }

    @RequestMapping("/charge")
    @ResponseBody
    @CrossOrigin
    public String charge(@AuthenticationPrincipal UserDetails userDetails) throws RazorpayException {
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        if (user==null) return null;
        Cart cart = cartJdbcDao.getCurrentCart(user.getUserId());
        if (cart==null) return null;
        Total total = getCartTotal(userDetails);
        PaymentObject paymentObject = new PaymentObject(publicKey, total.getFinalValue(), "Payment for cart "+cart.getCartId());
        Transactions transaction = new Transactions();
        transaction.setTransactionStatus(0);
        transaction.setCartId(cart.getCartId());
        var rzp = paymentService.charge(paymentObject, transaction);
        transaction.setTransactionId((String) rzp.toJson().get("id"));
        transactionJdbcDao.createTransaction(transaction);
        return rzp.toString();
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("payment_error", ex.getMessage());
        return "redirect:/cart/view";
    }

    @PostMapping(value = "/success/")
    @ResponseBody
    public String success(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("razorpay_payment_id") String razorpay_payment_id, @RequestParam("razorpay_order_id") String razorpay_order_id, @RequestParam("razorpay_signature") String razorpay_signature) throws RazorpayException {
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        if (user == null) return null;
        Cart cart = cartJdbcDao.getCurrentCart(user.getUserId());
        if (!paymentService.checkValidity(razorpay_order_id, razorpay_payment_id, razorpay_signature)){
            return "Payment failed!";
        }
        Transactions transaction = transactionJdbcDao.getTransactionById(razorpay_order_id);
        if (transaction==null) return "Payment failed!";
        transaction.setTransactionStatus(1);
        transactionJdbcDao.updateTransaction(transaction);
        long status = orderService.createOrder(user, cart, getCartTotal(userDetails));
        if (status != 0){ // failure
            paymentService.initiateRefund(razorpay_order_id);
            return "No drivers available!";
        }
        cart.setStatus(true);
        cartJdbcDao.updateCart(cart);
        return "Order placed successfully!";
    }

    @PostMapping(value="/assign_address", consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String assign_address(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("address_type") String address_type, @RequestParam("street_address") String street_address, @RequestParam("pincode") String pincode, @RequestParam("addressId") String addressId) {
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        if (user==null) return null;
        Cart cart = cartJdbcDao.getCurrentCart(user.getUserId());
        if (cart==null) return null;
        UserAddress userAddress = null;
        if (address_type.equals("new")){
            userAddress = new UserAddress(street_address, Long.parseLong(pincode), user.getUserId());
            userAddressJdbcDao.createUserAddress(userAddress);
        }
        else userAddress = userAddressJdbcDao.getUserAddressById(Long.parseLong(addressId));
        if (userAddress==null) return null;
        cart.setAddressId(userAddress.getAddressId());
        cartJdbcDao.updateCart(cart);
        return "success";
    }
}

