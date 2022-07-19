package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;

import java.util.List;

/**
 * @Author: liuyue
 * @DateTime: 2022/7/20 0:47
 * @Description: TODO
 */
public class GroupVo extends AttrGroupEntity {

    private List<AttrEntity> attrEntities;

    public List<AttrEntity> getAttrEntities() {
        return attrEntities;
    }

    public void setAttrEntities(List<AttrEntity> attrEntities) {
        this.attrEntities = attrEntities;
    }
}
