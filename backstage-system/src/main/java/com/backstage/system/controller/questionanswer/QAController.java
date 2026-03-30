package com.backstage.system.controller.questionanswer;

import com.backstage.system.service.impl.user.OshUserServiceImpl;
import com.backstage.system.service.questionanswer.IQAService;
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
    @Autowired
    private OshUserServiceImpl userService;
    //todo

//    @Anonymous
//    @ApiOperation("新增问题")
//    @PostMapping("/save")
//    public R<String> addQuestionAnswer(
//            @ApiParam("token") @RequestHeader(value = "token") String token,
//            @RequestBody AddQuestionDTO addQuestionDTO) {
//
//        return iqaService.addQuestionAnswer(userService.getUserIdByToken(token), addQuestionDTO.getResourceNo(),
//                addQuestionDTO.getResourceType(), addQuestionDTO.getTitle(),
//                addQuestionDTO.getContent(), addQuestionDTO.getIsPaidOnly(), addQuestionDTO.getTags());
//    }
}