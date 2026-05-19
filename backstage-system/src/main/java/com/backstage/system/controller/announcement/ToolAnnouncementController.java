package com.backstage.system.controller.announcement;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.announcement.ToolAnnouncementVO;
import com.backstage.system.service.announcement.IToolAnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "工具公告")
@RestController
@RequestMapping("/pc/announcement/tool")
public class ToolAnnouncementController {

    @Autowired
    private IToolAnnouncementService toolAnnouncementService;

    @ApiOperation("查询工具模块最新公告")
    @GetMapping("/latest")
    @Anonymous
    public R<List<ToolAnnouncementVO>> listLatest() {
        return R.ok(toolAnnouncementService.listLatestToolAnnouncements());
    }
}
