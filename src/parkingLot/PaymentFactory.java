package parkingLot;

public class PaymentFactory{
    public static boolean completePayment(double price, PaymentMode paymentMode) {
        switch (paymentMode) {
            case CASH:
                IO.println("Payment completed through cash");
                break;
            case UPI:
                IO.println("Payment completed through upi");
                break;
            case CARD:
                IO.println("Payment completed through card");
                break;
            default:
                IO.println("Payment failed");
                return  false;
        }
        
        return true;
    }
}
