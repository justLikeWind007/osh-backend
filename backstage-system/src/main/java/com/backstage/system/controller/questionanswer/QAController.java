package com.backstage.system.controller.questionanswer;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.questionanswer.dto.CheckQuestionPermissionDTO;
import com.backstage.system.service.questionanswer.IQAService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:08
 */
@RestController
@RequestMapping("/question/answer")
public class QAController {

    @Autowired
    private IQAService iqaService;

    public R<String> checkQuestionPermission(
            @ApiParam("token") @RequestHeader(value = "token") String token,
            @RequestBody CheckQuestionPermissionDTO checkQuestionPermissionDTO) {
        return iqaService.checkQuestionPermission(token, checkQuestionPermissionDTO.getResource_type(),checkQuestionPermissionDTO.getResource_id());
    }
    @Anonymous
    @ApiOperation("新增问题")
    @PostMapping("/add")
    public R<String> addQuestionAnswer(
            @ApiParam("token") @RequestHeader(value = "token") String token) {
        return R.ok();
    }
}