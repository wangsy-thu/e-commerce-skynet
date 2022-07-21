package edu.neu.ecommerce.statistics.constant;

import edu.neu.ecommerce.vo.OrderSumVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StatisticsConstant {
    public static List<OrderSumVo> ORDER_SUM_LIST;

    static {
        ORDER_SUM_LIST = new ArrayList<>();
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(3472),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(7641),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(9834),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(421),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(2760),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(5612),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(5707),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(3856),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(3490),1L,1L));
        ORDER_SUM_LIST.add(new OrderSumVo(1L, new BigDecimal(8721),1L,1L));
    }
}
