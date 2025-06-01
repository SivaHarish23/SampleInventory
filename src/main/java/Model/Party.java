package Model;

public class Party {
    protected Integer id;
    protected String name;
    protected String location;
    protected String phoneNumber;
    protected Long createdAt;
    protected Long updatedAt;
    protected Type type;

    public enum Type {
        CUSTOMER, VENDOR
    }

    // Private constructor
    protected Party(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.location = builder.location;
        this.phoneNumber = builder.phoneNumber;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.type = builder.type;
    }

    // Getters and setters ...

    @Override
    public String toString() {
        return "Party{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", type=" + type +
                '}';
    }

    // Builder class
    public static class Builder {
        private Integer id;
        private String name;
        private String location;
        private String phoneNumber;
        private Long createdAt;
        private Long updatedAt;
        private Type type;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder createdAt(Long createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Long updatedAt) { this.updatedAt = updatedAt; return this; }


        public Party build() {
            return new Party(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}