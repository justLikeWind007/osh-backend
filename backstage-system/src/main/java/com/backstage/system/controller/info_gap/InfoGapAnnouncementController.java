package com.backstage.system.controller.info_gap;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.dto.info_gap.InfoGapAnnoRespDTO;
import com.backstage.system.service.info_gap.InfoGapAnnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pc/info_gap/announcement")
public class InfoGapAnnouncementController {

    @Autowired
    private InfoGapAnnoService infoGapAnnoService;

    @GetMapping("/list/systemNotice")
    @Anonymous
    public R<List<InfoGapAnnoRespDTO>> listSystemNotices() {
        return R.ok(infoGapAnnoService.listSystemNotices());
    }

    @GetMapping("/list/userNotice")
    @Anonymous
    public R<List<InfoGapAnnoRespDTO>> listUserNotices() {
        return R.ok(infoGapAnnoService.listUserNotices());
    }
}
