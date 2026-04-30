package com.backstage.system.controller.group;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.vo.GroupColumnVo;
import com.backstage.system.domain.vo.GroupCourseVo;
import com.backstage.system.domain.vo.GroupWorkVo;
import com.backstage.system.service.IGroupService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/3
 * Time: 20:53
 */
@RestController
@RequestMapping("/pc/group")
public class GroupController extends BaseController {

    @Autowired
    private IGroupService iGroupService;

    @Anonymous
    @ApiOperation("查看拼团课程详情")
    @GetMapping("/course/read")
    public R<GroupCourseVo> course(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @ApiParam("课程ID") @RequestParam(value = "id") Long id,
            @ApiParam("拼团ID") @RequestParam(value = "groupId") Long groupId) {
        return R.ok(iGroupService.course(id, groupId));
    }

    @Anonymous
    @ApiOperation("查看拼团专栏详情")
    @GetMapping("/column/read")
    public R<GroupColumnVo> column(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token", required = false) String token,
            @ApiParam("课程ID") @RequestParam(value = "id") Long id,
            @ApiParam("拼团ID") @RequestParam(value = "groupId") Long groupId) {
        return R.ok(iGroupService.column(id, groupId));
    }

    @Anonymous
    @ApiOperation("当前拼团专栏or课程的可组团列表")
    @GetMapping("/list")
    public TableDataInfo list(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("拼团ID") @RequestParam(value = "groupId") Long groupId,
            @ApiParam("页码") @RequestParam(value = "page") Integer page) {
        startPage();
        List<GroupWorkVo> list = iGroupService.selectGroupList(groupId);
        return getDataTable(list);
    }
}