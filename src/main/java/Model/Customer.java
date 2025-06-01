package Model;


public class Customer extends Party {

    public Customer(Builder builder) {
        super(builder);
        this.setType(Type.CUSTOMER);  // override or enforce the type here
    }
}