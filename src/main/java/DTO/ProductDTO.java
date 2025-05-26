package DTO;


import java.math.BigDecimal;

public class ProductDTO {
    private int id;
    private String name;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int stockInHand;
    private int committedStock;
    private int orderedStock;
    private int openingStock;

    public ProductDTO() {}

    public ProductDTO(int id, String name, BigDecimal costPrice, BigDecimal sellingPrice,
                      int stockInHand, int committedStock, int orderedStock, int openingStock) {
        this.id = id;
        this.name = name;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockInHand = stockInHand;
        this.committedStock = committedStock;
        this.orderedStock = orderedStock;
        this.openingStock = openingStock;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public int getStockInHand() { return stockInHand; }
    public void setStockInHand(int stockInHand) { this.stockInHand = stockInHand; }

    public int getCommittedStock() { return committedStock; }
    public void setCommittedStock(int committedStock) { this.committedStock = committedStock; }

    public int getOrderedStock() { return orderedStock; }
    public void setOrderedStock(int orderedStock) { this.orderedStock = orderedStock; }

    public int getOpeningStock() { return openingStock; }
    public void setOpeningStock(int openingStock) { this.openingStock = openingStock; }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", costPrice=" + costPrice +
                ", sellingPrice=" + sellingPrice +
                ", stockInHand=" + stockInHand +
                ", committedStock=" + committedStock +
                ", orderedStock=" + orderedStock +
                ", openingStock=" + openingStock +
                '}';
    }
}
