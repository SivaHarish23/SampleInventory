package Model;

public class Party {
    protected Integer id;
    protected String name;
    protected String location;
    protected String phone_number;
    protected Long created_at;
    protected Long updated_at;
    protected Type type;

    public enum Type {
        CUSTOMER, VENDOR
    }

    public Party() {
    }

    // Private constructor
    protected Party(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.location = builder.location;
        this.phone_number = builder.phone_number;
        this.created_at = builder.created_at;
        this.updated_at = builder.updated_at;
        this.type = builder.type;
    }

    // Getters and setters ...

    @Override
    public String toString() {
        return "Party{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", type=" + type +
                '}';
    }

    // Builder class
    public static class Builder {
        private Integer id;
        private String name;
        private String location;
        private String phone_number;
        private Long created_at;
        private Long updated_at;
        private Type type;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder phone_number(String phone_number) { this.phone_number = phone_number; return this; }
        public Builder created_at(Long created_at) { this.created_at = created_at; return this; }
        public Builder updated_at(Long updated_at) { this.updated_at = updated_at; return this; }


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
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public Long getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Long created_at) {
        this.created_at = created_at;
    }

    public Long getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(Long updated_at) {
        this.updated_at = updated_at;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}