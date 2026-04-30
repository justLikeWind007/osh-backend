package com.backstage.system.service.questionanswer.impl;

import cn.hutool.core.bean.BeanUtil;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.questionanswer.Tag;
import com.backstage.system.domain.questionanswer.vo.QATagVO;
import com.backstage.system.mapper.questionanswer.OshQATagMapper;
import com.backstage.system.service.questionanswer.IOshQATagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/1
 * Time: 15:37
 */
@Service
public class OshQATagServiceImpl implements IOshQATagService {

    @Autowired
    private OshQATagMapper oshQaMapper;
    @Override
    public R<List<QATagVO>> searchTags(String type) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<Tag>().select(Tag::getId, Tag::getName)
                .eq(StringUtils.hasText(type), Tag::getType, type)
                .orderByDesc(Tag::getUseCount);
        List<Tag> tags = oshQaMapper.selectList(wrapper);
        List<QATagVO> QATagVOS = new ArrayList<>();
        for (Tag tag : tags) {
            QATagVO QATagVO = new QATagVO();
            BeanUtil.copyProperties(tag, QATagVO);
            QATagVOS.add(QATagVO);
        }
        return R.ok(QATagVOS);
    }
}
