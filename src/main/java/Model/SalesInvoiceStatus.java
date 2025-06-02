package Model;

public enum SalesInvoiceStatus {
    PAID(0),
    DELIVERED(1);

    private final int code;

    SalesInvoiceStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SalesInvoiceStatus fromCode(int code) {
        for (SalesInvoiceStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }

    public static SalesInvoiceStatus fromString(String name) {
        for (SalesInvoiceStatus status : values()) {
            if (status.name().equalsIgnoreCase(name)) return status;
        }
        throw new IllegalArgumentException("Invalid status name: " + name);
    }

    public static int codeFromString(String name) {
        return fromString(name).getCode();
    }

    public static int getCode(String name) {
        return fromString(name).getCode();
    }
    public static String getString(int code) {
        return fromCode(code).name();
    }
}