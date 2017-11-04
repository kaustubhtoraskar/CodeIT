package svkt.wallet.models;

/**
 * Created by Hanumaan on 11/4/2017.
 */

public class Transaction {
    public String to;
    public String from;
    public String amount;
    public String date;
    public String  type;

    public Transaction(){
    }

    public Transaction(String to,String from,String amount,String date, String type){
        this.to = to;
        this.from = from;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }
}
