package sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuSaleVo {

    private Long skuId;

    // 积分相关字段:成长积分、购物积分、是否优惠下的积分生效情况
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;
     * 0 - 无优惠，成长积分是否赠送;
     * 1 - 无优惠，购物积分是否赠送;
     * 2 - 有优惠，成长积分是否赠送;
     * 3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    // 打折相关信息：满几件、打几折、可否与其他优惠叠加[0-不可叠加，1-可叠加]
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;

    // 满减相关信息：满几元、减几元、可否与其他优惠叠加[0-不可叠加，1-可叠加]
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
}
