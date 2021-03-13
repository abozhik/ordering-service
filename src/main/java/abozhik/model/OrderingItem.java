package abozhik.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderingItem {

    private Long id;
    private Long orderingId;
    private String itemName;
    private Long itemCount;
    private BigDecimal itemPrice;

}
