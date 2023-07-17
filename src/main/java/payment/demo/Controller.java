package payment.demo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class Controller {
    private final PaymentService paymentService;
    @PostMapping("/createPayment")
    public ResponseEntity<?> createPayment(HttpServletRequest req, @RequestBody Payment payment) throws UnsupportedEncodingException {
        try {
            return paymentService.createPayment(req, payment);
        } catch (Exception e) {
            throw e;
        }
    }
    @GetMapping("/returnPayment")
    public ResponseEntity<?> paymentReturn(
            HttpServletRequest request
    ) throws UnsupportedEncodingException {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNpayConfig.hashAllFields(fields);

        TransactionData transactionData = new TransactionData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        if (signValue.equals(request.getParameter("vnp_SecureHash"))) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                return ResponseEntity.ok("Payment handled successfully");
            } else {
                return ResponseEntity.ok("Payment handled failed");
            }
        } else {
            return ResponseEntity.ok("Payment handled failed 0");
        }

    }
}
