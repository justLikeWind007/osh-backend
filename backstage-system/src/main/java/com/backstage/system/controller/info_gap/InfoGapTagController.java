package com.backstage.system.controller.info_gap;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.dto.info_gap.InfoGapTagListRespDTO;
import com.backstage.system.service.info_gap.InfoGapTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
