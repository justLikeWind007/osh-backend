package com.backstage.system.controller.website;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.service.website.impl.WebsiteEsService;
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
 * 实用网站 ES 索引管理 Controller
 *
 * 提供两个运维接口：
 *   1. 重建索引：删除旧索引，按 mapping 文件重新创建
 *   2. 全量同步：把 MySQL 中所有已审核通过的网站同步到 ES
 *
 * 这两个接口通常只在以下场景手动调用：
 *   - 首次上线，ES 索引为空
 *   - 索引 mapping 变更，需要重建
 *   - ES 数据异常，需要从 MySQL 重新同步
 */
@Api(tags = "实用网站 ES 索引管理")
@RestController
@RequestMapping("/pc/website/es")
public class WebsiteEsIndexController {

    @Autowired
    private WebsiteEsService websiteEsService;

    /**
     * 重建网站 ES 索引
     * 读取 classpath 下的 mapping 定义文件，删除旧索引后重新创建
     */
    @ApiOperation("重建网站 ES 索引")
    @Anonymous
    @PostMapping("/recreateIndex")
    public R<String> recreateIndex() {
        try {
            // 读取 resources/es/osh_practical_website_index.json
            ClassPathResource resource = new ClassPathResource("es/osh_practical_website_index.json");
            String indexDefinitionJson = StreamUtils.copyToString(
                resource.getInputStream(), StandardCharsets.UTF_8
            );
            websiteEsService.recreateIndex(indexDefinitionJson);
            return R.ok("网站 ES 索引重建成功");
        } catch (Exception e) {
            return R.fail("索引重建失败：" + e.getMessage());
        }
    }

    /**
     * 全量同步网站到 ES
     * 从 MySQL 查出所有已审核通过的网站，清空 ES 后批量写入
     */
    @ApiOperation("全量同步网站到 ES")
    @Anonymous
    @PostMapping("/syncAll")
    public R<Integer> syncAll() {
        try {
            int count = websiteEsService.syncAllToEs();
            return R.ok(count, "全量同步完成，共同步 " + count + " 条网站");
        } catch (Exception e) {
            return R.fail("全量同步失败：" + e.getMessage());
        }
    }
}
