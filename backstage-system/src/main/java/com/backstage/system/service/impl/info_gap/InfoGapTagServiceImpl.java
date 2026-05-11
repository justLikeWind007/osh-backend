package com.backstage.system.service.impl.info_gap;

import com.backstage.system.domain.dto.info_gap.InfoGapTagListRespDTO;
import com.backstage.system.domain.info_gap.OshInfoGapTag;
import com.backstage.system.mapper.info_gap.OshInfoGapTagMapper;
import com.backstage.system.service.info_gap.InfoGapTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfoGapTagServiceImpl implements InfoGapTagService {

    @Autowired
    private OshInfoGapTagMapper oshInfoGapTagMapper;

    @Override
    public List<InfoGapTagListRespDTO> getTagList() {

        LambdaQueryWrapper<OshInfoGapTag> queryWrapper = Wrappers.lambdaQuery(OshInfoGapTag.class)
                .eq(OshInfoGapTag::getDeleteFlag, 0)
                .eq(OshInfoGapTag::getStatus, 1);

        List<OshInfoGapTag> oshInfoGapTags = oshInfoGapTagMapper.selectList(queryWrapper);

        List<InfoGapTagListRespDTO> result = oshInfoGapTags.stream().map(tag -> {
            InfoGapTagListRespDTO respDTO = new InfoGapTagListRespDTO();
            respDTO.setTagName(tag.getTagName());
            respDTO.setStatus(tag.getStatus());
            respDTO.setTagUseCount(tag.getTagUseCount());
            return respDTO;
        }).collect(Collectors.toList());

        return result;
    }
}
