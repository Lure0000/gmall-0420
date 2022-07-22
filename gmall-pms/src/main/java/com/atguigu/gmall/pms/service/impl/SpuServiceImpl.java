package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sms.vo.SkuSaleVo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private SpuAttrValueService baseAttrService;

    @Autowired
    private SkuImagesService imagesService;

    @Autowired
    private SpuDescService descService;

    @Autowired
    private SkuAttrValueService attrValueService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuByCidPage(Long cid, PageParamVo pageParamVo) {

        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();

        // 分类id不为0，说明要查本类，如果为0，说明查询全站
        if (cid != 0) {
            wrapper.eq("category_id", cid);
        }

        // 查询关键字
        String key = pageParamVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(t -> t.eq("id", key).or().like("name", key));
        }

        // 使用mp的分页查询方法
        IPage<SpuEntity> page = this.page(
                pageParamVo.getPage(),
                wrapper
        );

        return new PageResultVo(page);
    }

    @Override
    //@Transactional(rollbackFor = Exception.class, timeout = 10000, readOnly = false)
    @GlobalTransactional
    public void bigSave(SpuVo spu) throws FileNotFoundException {
        // 1.保存spu相关信息
        // (1)保存spu基本信息pms_spu
        Long spuId = saveSpu(spu);
        // (2)保存spu的描述信息pms_spu_desc
        this.descService.saveSpuDesc(spu, spuId);
        // (3)保存spu的规格参数信息pms_spu_attr_value
        this.saveBaseAttrs(spu, spuId);

        /**
         * 	传播行为：一个service的方法调用另一个service的方法时，事务之间的影响就是传播行为
         * 		required：同一个事务
         * 		requires_new：不同事务
         * 		同一个service传播行为不能生效，不同service传播行为才能生效
         *
         * 	回滚策略：
         * 		默认：编译时（受检异常）异常不回滚，运行时（不受检异常）异常才回滚
         * 		自定义：rollbackFor/rollbackForClassName
         * 			noRollbackFor/noRollbackForClassName
         *
         * 	超时事务timeout
         * 	只读事务readOnly默认为false
         *
         * 	        try {
         *             TimeUnit.SECONDS.sleep(4);
         *         } catch (InterruptedException e) {
         *             e.printStackTrace();
         *         }
         *
         *         int i = 1/0;
         *         new FileInputStream("xxxx");//编译时异常
         *
         * */

        // 2.保存sku相关信息
        this.saveSkus(spu, spuId);
    }

    /**
     * 保存sku相关信息及营销信息
     *
     * @param spu
     * @param spuId
     */
    private void saveSkus(SpuVo spu, Long spuId) {
        List<SkuVo> skus = spu.getSkus();
        if (CollectionUtils.isEmpty(skus)) {
            return;
        }
        skus.forEach(skuVo -> {
            // (1)保存pms_sku
            skuVo.setSpuId(spuId);
            skuVo.setCatagoryId(spu.getCategoryId());
            skuVo.setBrandId(spu.getBrandId());
            List<String> images = skuVo.getImages();
            if (!CollectionUtils.isEmpty(images)) {
                skuVo.setDefaultImage(StringUtils.isNotBlank(skuVo.getDefaultImage()) ? skuVo.getDefaultImage() : images.get(0));
            }
            this.skuMapper.insert(skuVo);
            Long skuId = skuVo.getId();

            // (2)保存pms_sku_images
            if (!CollectionUtils.isEmpty(images)) {
                imagesService.saveBatch(images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setUrl(image);
                    skuImagesEntity.setSort(0);
                    if (StringUtils.equals(image, skuVo.getDefaultImage())) {
                        skuImagesEntity.setDefaultStatus(1);
                    }
                    return skuImagesEntity;
                }).collect(Collectors.toList()));
            }

            // (3)保存pms_sku_attr_value
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)) {
                saleAttrs.forEach(attr -> {
                    attr.setSkuId(skuId);
                    attr.setSort(0);
                });
                this.attrValueService.saveBatch(saleAttrs);
            }

            // 3.保存sku营销信息
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuVo, skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            this.smsClient.saveSkuSaleInfo(skuSaleVo);
        });
    }

    /**
     * 保存spu基本属性信息
     *
     * @param spu
     * @param spuId
     */
    private void saveBaseAttrs(SpuVo spu, Long spuId) {
        List<SpuAttrValueVo> baseAttrs = spu.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            this.baseAttrService.saveBatch(baseAttrs.stream().map(spuAttrValueVo -> {
                SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(spuAttrValueVo, spuAttrValueEntity);
                spuAttrValueEntity.setSpuId(spuId);
                spuAttrValueEntity.setSort(0);
                return spuAttrValueEntity;
            }).collect(Collectors.toList()));
        }
    }

    /**
     * 保存spu基本信息
     *
     * @param spu
     * @return
     */
    private Long saveSpu(SpuVo spu) {
        spu.setCreateTime(new Date());
        spu.setUpdateTime(spu.getCreateTime());
        this.save(spu);
        return spu.getId();
    }

}

/**
 * stream常用方法
 */
class Test {
    public static void main(String[] args) {

        List<User> users = Arrays.asList(
                new User(1L, "柳岩", 20),
                new User(2L, "马苏", 21),
                new User(3L, "马蓉", 22),
                new User(4L, "小鹿", 23),
                new User(5L, "柏芝", 24),
                new User(6L, "凤姐", 25),
                new User(7L, "大幂幂", 26)
        );

        // 集合转化map
        List<Person> persons = users.stream().map(user -> {
            Person person = new Person();
            person.setId(user.getId());
            person.setUserName(user.getName());
            person.setAge(user.getAge());
            return person;
        }).collect(Collectors.toList());
        System.out.println(persons);

        // 求和reduce
        List<Integer> aa = Arrays.asList(1, 2, 3, 4, 5, 6);
        System.out.println(aa.stream().reduce((a, b) -> a + b).get());
        System.out.println(users.stream().map(User::getAge).reduce((a, b) -> a + b).get());

        //  过滤filter
        System.out.println(users.stream().filter(user -> user.getAge() > 22).collect(Collectors.toList()));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
class User {
    private Long id;
    private String name;
    private Integer age;
}

@Data
@ToString
class Person {
    private Long id;
    private String userName;
    private Integer age;
}