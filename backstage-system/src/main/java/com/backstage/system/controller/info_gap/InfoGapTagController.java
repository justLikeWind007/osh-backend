package com.backstage.system.controller.info_gap;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.dto.info_gap.InfoGapTagListRespDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapUpdateReqDTO;
import com.backstage.system.service.info_gap.InfoGapTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pc/info_gap/tag")
public class InfoGapTagController {

    @Autowired
    private InfoGapTagService infoGapTagService;

    /**
     * 获取标签列表
     */
    @Anonymous
    @GetMapping("/list")
    public R<List<InfoGapTagListRespDTO>> tagList() {
        List<InfoGapTagListRespDTO> tagList = infoGapTagService.getTagList();

        return R.ok(tagList);
    }

    /**
     * 获取推荐标签列表
     */
    @Anonymous
    @GetMapping("/list/recommend")
    public R<List<InfoGapTagListRespDTO>> recommendTagList() {
        List<InfoGapTagListRespDTO> tagList = infoGapTagService.getRecommendTagList();

        return R.ok(tagList);
    }

    /**
     * 新增标签
     */
    @GetMapping("/add")
    public R<Void> addTag(@RequestParam("tagName") String tagName) {
        infoGapTagService.addTag(tagName);
        return R.ok();
    }

    /**
     * 删除标签
     */
    @GetMapping("/delete")
    public R<Void> deleteTag(@RequestParam("id") Long id) {
        infoGapTagService.deleteTag(id);
        return R.ok();
    }
}
