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
    public float balance;

    public User(){
    }

    public User(String name, String email, String contactNo, String cardNo, String cardExpiryDate,float balance){
        this.name = name;
        this.email = email;
        this.contactNo = contactNo;
        this.cardNo = cardNo;
        this.cardExpiryDate = cardExpiryDate;
        this.balance = balance;
    }
}