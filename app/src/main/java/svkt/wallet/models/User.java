package svkt.wallet.models;

/**
 * Created by Hanumaan on 11/4/2017.
 */

public class User {

    public String name;
    public String email;
    public String contactNo;
    public String cardNo;
    public String cardExpiryDate;
    public String address;

    public User(){
    }

    public User(String name, String email, String contactNo, String cardNo, String cardExpiryDate, String address){
        this.name = name;
        this.email = email;
        this.contactNo = contactNo;
        this.cardNo = cardNo;
        this.cardExpiryDate = cardExpiryDate;
        this.address = address;
    }
}
