package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.SpuService;
import com.atguigu.gmall.pms.vo.SpuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * spu信息
 *
 * @author liuyue
 * @email 1539847706@qq.com
 * @date 2022-07-18 17:15:29
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spu")
public class SpuController {

    @Autowired
    private SpuService spuService;

    @Autowired
    private GmallSmsClient smsClient;

    @ApiOperation("按照分类id分页查询商品列表 0-全站 n-本类")
    @GetMapping("category/{categoryId}")
    public ResponseVo<PageResultVo> querySpuByCidPage(@PathVariable("categoryId") Long cid, PageParamVo pageParamVo) {
        PageResultVo resultVo = this.spuService.querySpuByCidPage(cid, pageParamVo);
        return ResponseVo.ok(resultVo);
    }

    @ApiOperation("分页查询已上架SPU")
    @PostMapping("pageJson")
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo) {
        PageResultVo pageResultVo = spuService.queryPage(paramVo);
        List<SpuEntity> list = (List<SpuEntity>) pageResultVo.getList();
        return ResponseVo.ok(list);
    }

    /**
     * 大保存
     */
    @PostMapping
    @ApiOperation("大保存")
    public ResponseVo<Object> save(@RequestBody SpuVo spu) throws FileNotFoundException {
        this.spuService.bigSave(spu);
        return ResponseVo.ok();
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySpuByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = spuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id) {
        SpuEntity spu = spuService.getById(id);

        return ResponseVo.ok(spu);
    }


    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SpuEntity spu) {
        spuService.updateById(spu);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        spuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
