package com.atguigu.gmall.pms.feign;

import org.springframework.cloud.openfeign.FeignClient;
import sms.api.GmallSmsApi;

@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {


}
