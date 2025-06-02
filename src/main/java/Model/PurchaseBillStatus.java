package Model;

public enum PurchaseBillStatus {
    PAID(0),
    RECEIVED(1);

    private final int code;

    PurchaseBillStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PurchaseBillStatus fromCode(int code) {
        for (PurchaseBillStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }

    public static PurchaseBillStatus fromString(String name) {
        for (PurchaseBillStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) return status;
        }
        throw new IllegalArgumentException("Invalid status name: " + name);
    }

    public static int getCode(String name) {
        return fromString(name).getCode();
    }
    public static String getString(int code) {
        return fromCode(code).name();
    }
}