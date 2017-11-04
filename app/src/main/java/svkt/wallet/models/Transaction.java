package svkt.wallet.models;

/**
 * Created by Hanumaan on 11/4/2017.
 */

public class Transaction {
    public String to;
    public String from;
    public String amount;
    public String date;

    public Transaction(){
    }

    public Transaction(String to,String from,String amount,String date){
        this.to = to;
        this.from = from;
        this.amount = amount;
        this.date = date;
    }
}
