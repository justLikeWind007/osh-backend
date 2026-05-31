package com.backstage.system.service.impl.info_gap;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.dto.info_gap.InfoGapTagListRespDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapTagReqDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapUpdateReqDTO;
import com.backstage.system.domain.info_gap.OshInfoGap;
import com.backstage.system.domain.info_gap.OshInfoGapTag;
import com.backstage.system.domain.info_gap.OshInfoGapTagRel;
import com.backstage.system.mapper.info_gap.OshInfoGapTagMapper;
import com.backstage.system.mapper.info_gap.OshInfoGapTagRelMapper;
import com.backstage.system.service.info_gap.InfoGapTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfoGapTagServiceImpl implements InfoGapTagService {

    @Autowired
    private OshInfoGapTagMapper oshInfoGapTagMapper;
    @Autowired
    private OshInfoGapTagRelMapper oshInfoGapTagRelMapper;

    @Override
    public List<InfoGapTagListRespDTO> getTagList() {

        LambdaQueryWrapper<OshInfoGapTag> queryWrapper = Wrappers.lambdaQuery(OshInfoGapTag.class)
                .eq(OshInfoGapTag::getDeleteFlag, 0)
                .eq(OshInfoGapTag::getStatus, 1);

        List<OshInfoGapTag> oshInfoGapTags = oshInfoGapTagMapper.selectList(queryWrapper);

        List<InfoGapTagListRespDTO> result = oshInfoGapTags.stream().map(tag -> {
            InfoGapTagListRespDTO respDTO = new InfoGapTagListRespDTO();
            respDTO.setId(tag.getId());
            respDTO.setTagName(tag.getTagName());
            respDTO.setStatus(tag.getStatus());
            respDTO.setTagUseCount(tag.getTagUseCount());
            return respDTO;
        }).collect(Collectors.toList());

        return result;
    }

    @Override
    public List<InfoGapTagListRespDTO> getRecommendTagList() {
        return getTagList().stream()
                .sorted(Comparator.comparing(
                        InfoGapTagListRespDTO::getTagUseCount,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(8)
                .collect(Collectors.toList());
    }

    @Override
    public void addTag(String tagName) {
        LambdaQueryWrapper<OshInfoGapTag> queryWrapper = Wrappers.lambdaQuery(OshInfoGapTag.class)
                .eq(OshInfoGapTag::getTagName, tagName);

        Long existTagName = oshInfoGapTagMapper.selectCount(queryWrapper);

        if (existTagName != null && existTagName > 0) {
            throw new ServiceException("标签已存在");
        }

        OshInfoGapTag entity = new OshInfoGapTag();
        entity.setTagName(tagName);
        entity.setCreateTime(LocalDateTime.now());

        int rows = oshInfoGapTagMapper.insert(entity);
        if (rows <= 0) {
            throw new ServiceException("添加标签失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        LambdaQueryWrapper<OshInfoGapTag> queryWrapper = Wrappers.lambdaQuery(OshInfoGapTag.class)
                .eq(OshInfoGapTag::getId, id)
                .eq(OshInfoGapTag::getDeleteFlag, 0);

        OshInfoGapTag currentTag = oshInfoGapTagMapper.selectOne(queryWrapper);
        if (currentTag == null) {
            throw new ServiceException("标签不存在");
        }

        LambdaQueryWrapper<OshInfoGapTagRel> queryWrapper1 = Wrappers.lambdaQuery(OshInfoGapTagRel.class)
                .eq(OshInfoGapTagRel::getInfoGapId, id)
                .eq(OshInfoGapTagRel::getDeleteFlag, 0);

        Long relCount = oshInfoGapTagRelMapper.selectCount(queryWrapper1);
        if (relCount != null && relCount > 0) {
            throw new ServiceException("标签已关联信息差，请先解除关联关系");
        }

        LambdaUpdateWrapper<OshInfoGapTag> deleteWrapper = Wrappers.lambdaUpdate(OshInfoGapTag.class)
                .eq(OshInfoGapTag::getId, id);
        int rows = oshInfoGapTagMapper.delete(deleteWrapper);
        if (rows <= 0) {
            throw new ServiceException("删除标签失败");
        }
    }
}
