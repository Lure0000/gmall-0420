package com.atguigu.gmall.sms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券领取历史记录
 * 
 * @author liuyue
 * @email 1539847706@qq.com
 * @date 2022-07-18 18:57:55
 */
@Data
@TableName("sms_coupon_history")
public class CouponHistoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 优惠券id
	 */
	private Long couponId;
	/**
	 * 会员id
	 */
	private Long memberId;
	/**
	 * 会员名字
	 */
	private String memberNickName;
	/**
	 * 获取方式[0->后台赠送；1->主动领取]
	 */
	private Integer getType;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 使用状态[0->未使用；1->已使用；2->已过期]
	 */
	private Integer useType;
	/**
	 * 使用时间
	 */
	private Date useTime;
	/**
	 * 订单id
	 */
	private Long orderId;
	/**
	 * 订单号
	 */
	private Long orderSn;

}
