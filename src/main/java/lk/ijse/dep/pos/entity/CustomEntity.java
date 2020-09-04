package lk.ijse.dep.pos.entity;

import lombok.*;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.sql.Date;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomEntity implements SuperEntity {

    private String orderId;
    private Date orderDate;
    private String customerId;
    private String customerName;
    private BigDecimal total;



}
