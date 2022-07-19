package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

/**
 * spuInfo扩展对象
 * 包含：spuInfo基本信息、spuImages图片信息、baseAttrs基本属性信息、skus信息
 */
@Data
public class SpuVo extends SpuEntity {

    // 图片信息
    private List<?> spuImages;

    // 基本属性信息
    private List<?> baseAttrs;

    // sku信息
    private List<?> skus;
}
