package Model;

public class Vendor extends Party {

    public Vendor(Builder builder) {
        super(builder);
        this.setType(Type.VENDOR);  // override or enforce the type here
    }
}