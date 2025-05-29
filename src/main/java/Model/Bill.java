package Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Bill {
    int getId();
    void setId(int id);

    String getStatus();
    void setStatus(String status);

    LocalDate getBill_date();
    void setBill_date(LocalDate bill_date);

    BigDecimal getAmount();
    void setAmount(BigDecimal amount);

    LocalDateTime getCreated_at();
    void setCreated_at(LocalDateTime createdAt);

    LocalDateTime getUpdated_at();
    void setUpdated_at(LocalDateTime updatedAt);

}
