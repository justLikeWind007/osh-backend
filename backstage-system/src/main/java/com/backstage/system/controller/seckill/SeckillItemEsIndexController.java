package com.backstage.system.controller.seckill;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.mapper.seckill.SeckillItemEsMapper;
import com.backstage.system.service.seckill.ISeckillItemEsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * 秒杀商品 ES 索引管理 Controller
 *
 * 提供两个运维接口：
 *   1. 重建索引：删除旧索引，按 mapping 文件重新创建（mapping 变更时使用）
 *   2. 全量同步：把进行中活动的商品明细从 MySQL 重新同步到 ES
 *
 * 通常在以下场景手动调用：
 *   - 首次上线，ES 索引为空
 *   - 索引 mapping 变更（如本次新增 tagNames 字段）
 *   - ES 数据异常，需要从 MySQL 重新同步
 */
@Api(tags = "秒杀商品 ES 索引管理")
@RestController
@RequestMapping("/pc/seckill/es")
public class SeckillItemEsIndexController {

    @Autowired
    private SeckillItemEsMapper seckillItemEsMapper;

    @Autowired
    private ISeckillItemEsService seckillItemEsService;

    /**
     * 重建秒杀商品 ES 索引
     * 读取 classpath 下的 mapping 定义文件，删除旧索引后重新创建
     * ⚠️ 执行后索引为空，需要紧接着调用 /syncAll 重新同步数据
     */
    @ApiOperation("重建秒杀商品 ES 索引（mapping 变更时使用）")
    @Anonymous
    @PostMapping("/recreateIndex")
    public R<String> recreateIndex() {
        try {
            ClassPathResource resource = new ClassPathResource("es/osh_seckill_item_search_index.json");
            String indexDefinitionJson = StreamUtils.copyToString(
                    resource.getInputStream(), StandardCharsets.UTF_8);
            seckillItemEsMapper.recreateSeckillItemSearchIndex(indexDefinitionJson);
            return R.ok("秒杀商品 ES 索引重建成功，请调用 /syncAll 重新同步数据");
        } catch (Exception e) {
            return R.fail("索引重建失败：" + e.getMessage());
        }
    }

    /**
     * 全量同步秒杀商品明细到 ES
     * 从 MySQL 查出所有进行中活动的商品明细，清空 ES 后批量写入
     */
    @ApiOperation("全量同步秒杀商品明细到 ES")
    @Anonymous
    @PostMapping("/syncAll")
    public R<Integer> syncAll() {
        try {
            int count = seckillItemEsService.syncAllItemsToEs();
            return R.ok(count, "全量同步完成，共同步 " + count + " 条明细");
        } catch (Exception e) {
            return R.fail("全量同步失败：" + e.getMessage());
        }
    }
}
