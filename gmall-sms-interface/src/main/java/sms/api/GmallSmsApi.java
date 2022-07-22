package sms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sms.vo.SkuSaleVo;

public interface GmallSmsApi {

    /**
     * 新增sku的营销信息
     */
    @PostMapping("sms/skubounds/skuSale/save")
    ResponseVo saveSkuSaleInfo(@RequestBody SkuSaleVo skuSaleVo);
}
