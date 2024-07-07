package com.dev.services;

import com.dev.utilities.PaymentObject;
import com.dev.models.Transactions;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
public class PaymentService {

    @Value("${RAZORPAY_SECRET_KEY}")
    private String API_SECRET_KEY;
    @Value("${RAZORPAY_PUBLIC_KEY}")
    private String API_PUBLIC_KEY;

    public String getAPI_SECRET_KEY() {
        return API_SECRET_KEY;
    }

    public void setAPI_SECRET_KEY(String API_SECRET_KEY) {
       this.API_SECRET_KEY = API_SECRET_KEY;
    }

    public String getAPI_PUBLIC_KEY() {
        return API_PUBLIC_KEY;
    }

    public void setAPI_PUBLIC_KEY(String API_PUBLIC_KEY) {
        this.API_PUBLIC_KEY = API_PUBLIC_KEY;
    }

    public Order charge(PaymentObject obj, Transactions transactions) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(API_PUBLIC_KEY, API_SECRET_KEY);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", obj.getAmount());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", ""+transactions.getTransactionId());
        Order order = razorpayClient.Orders.create(orderRequest);
        return order;
    }

    @ExceptionHandler(RazorpayException.class)
    public String handleRazorpayException(RazorpayException e) throws RazorpayException{
        throw new RazorpayException(e.getMessage());
    }

    public boolean checkValidity(String razorpay_order_id, String razorpay_payment_id, String razorpay_signature) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(API_PUBLIC_KEY, API_SECRET_KEY);
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", razorpay_order_id);
        options.put("razorpay_payment_id", razorpay_payment_id);
        options.put("razorpay_signature", razorpay_signature);
        return Utils.verifyPaymentSignature(options, API_SECRET_KEY);
    }

    public long initiateRefund(String order_id) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(API_PUBLIC_KEY, API_SECRET_KEY);
        var payments = razorpay.Orders.fetchPayments(order_id);
        for (var payment : payments) {
            if (payment.get("status").equals("captured")) {
                var refund = razorpay.Payments.refund(payment.get("id").toString());
                return 0;
            }
        }
        return -1;
    }
}
