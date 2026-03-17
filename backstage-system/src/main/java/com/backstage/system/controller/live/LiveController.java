package com.backstage.system.controller.live;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.vo.LiveDetailVo;
import com.backstage.system.domain.vo.LiveQueryVo;
import com.backstage.system.service.ILiveService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/8
 * Time: 00:34
 */
@RestController
@RequestMapping("/pc/live")
public class LiveController extends BaseController {

    private final ILiveService liveService;

    @Autowired
    public LiveController(ILiveService liveService) {
        this.liveService = liveService;
    }

    @Anonymous
    @ApiOperation(value = "查看直播详情")
    @GetMapping("/read")
    public R<LiveDetailVo> read(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token") String token,
            @ApiParam("直播ID") @RequestParam(value = "id") Long id) {
        return liveService.read(id, token);
    }

    @Anonymous
    @ApiOperation(value = "直播列表")
    @GetMapping("/list")
    public TableDataInfo list(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("页码") @RequestParam(value = "page") Integer page) {
        startPage();
        List<LiveQueryVo> liveQueryVoList = liveService.list();
        return getDataTable(liveQueryVoList);
    }
}