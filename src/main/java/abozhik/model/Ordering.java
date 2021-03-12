package abozhik.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Ordering {

    private Long id;
    private String userName;
    private boolean done;

    private List<OrderingItem> orderingItemList;

}
