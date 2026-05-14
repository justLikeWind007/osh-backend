package com.backstage.system.service.impl.info_gap;

import com.backstage.system.domain.info_gap.OshInfoGap;
import com.backstage.system.domain.info_gap.OshInfoGapCollect;
import com.backstage.system.mapper.info_gap.OshInfoGapCollectMapper;
import com.backstage.system.mapper.info_gap.OshInfoGapMapper;
import com.backstage.system.service.info_gap.InfoGapCollectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InfoGapCollectServiceImpl implements InfoGapCollectService {

    @Autowired
    private OshInfoGapCollectMapper infoGapCollectMapper;
    @Autowired
    private OshInfoGapMapper infoGapMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectInfoGap(Long userId, Long infoGapId) {
        OshInfoGap oshInfoGap = infoGapMapper.selectById(infoGapId);
        if (oshInfoGap == null) {
            throw new RuntimeException("信息差不存在!");
        }

        Long authorId = oshInfoGap.getUserId();
        if (authorId.equals(userId)) {
            throw new RuntimeException("不能收藏自己的信息差!");
        }

        LambdaQueryWrapper<OshInfoGapCollect> queryWrapper = Wrappers.lambdaQuery(OshInfoGapCollect.class)
                .eq(OshInfoGapCollect::getUserId, userId)
                .eq(OshInfoGapCollect::getInfoGapId, infoGapId)
                .eq(OshInfoGapCollect::getDeleteFlag, 0);

        OshInfoGapCollect oshInfoGapCollect = infoGapCollectMapper.selectOne(queryWrapper);

        // 未收藏 → 新增收藏
        if (oshInfoGapCollect == null) {
            OshInfoGapCollect entity = new OshInfoGapCollect();
            entity.setUserId(userId);
            entity.setInfoGapId(infoGapId);
            entity.setTargetUserId(authorId);

            infoGapCollectMapper.insert(entity);
            return;
        }

        // 已收藏 → 取消收藏
        LambdaUpdateWrapper<OshInfoGapCollect> updateWrapper = Wrappers.lambdaUpdate(OshInfoGapCollect.class)
                .eq(OshInfoGapCollect::getId, oshInfoGapCollect.getId())
                .set(OshInfoGapCollect::getDeleteFlag, 1);

        infoGapCollectMapper.update(null, updateWrapper);
    }
}
