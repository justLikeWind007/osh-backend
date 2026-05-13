package com.backstage.system.service.impl.info_gap;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.dto.info_gap.InfoGapCreateDTO;
import com.backstage.system.domain.info_gap.*;
import com.backstage.system.domain.user.risk.OshUserRiskProfile;
import com.backstage.system.domain.vo.info_gap.InfoGapVO;
import com.backstage.system.mapper.info_gap.*;
import com.backstage.system.service.info_gap.InfoGapService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfoGapServiceImpl implements InfoGapService {

    @Autowired
    private OshUserRiskProfileMapper riskMapper;
    @Autowired
    private OshInfoGapVoteMapper voteMapper;
    @Autowired
    private OshInfoGapMapper infoGapMapper;
    @Autowired
    private OshInfoGapFollowMapper followMapper;
    @Autowired
    private OshInfoGapTagRelMapper infoGapTagRelMapper;
    @Autowired
    private OshInfoGapTagMapper infoGapTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleFollow(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("操作无效：不能关注你自己");
        }
        OshInfoGapFollow existFollow = followMapper.selectFollowRecord(currentUserId, targetUserId);
        if (existFollow != null) {
            followMapper.deleteFollowRecord(existFollow.getId());
        } else {
            OshInfoGapFollow newFollow = new OshInfoGapFollow();
            newFollow.setUserId(currentUserId);
            newFollow.setTargetUserId(targetUserId);
            followMapper.insertFollowRecord(newFollow);
        }
    }

    @Override
    public List<InfoGapVO> getInfoGapList(Integer pageNum, Integer pageSize, String type, Long currentUserId) {
        if (type != null && type.equals("follow")) {
            PageHelper.startPage(pageNum, pageSize);
            return infoGapMapper.selectInfoGapPageForFollow(currentUserId);
        } else {
            PageHelper.startPage(pageNum, pageSize);
            return infoGapMapper.selectInfoGapPage(type, currentUserId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createInfoGap(InfoGapCreateDTO dto, Long userId) {
        if (dto.getContent() == null || dto.getContent().length() > 500) {
            throw new RuntimeException("内容不能为空且不能超过500字");
        }

        // 手动查询风控信息
        OshUserRiskProfile risk = riskMapper.selectRiskByUserId(userId);
        if (risk != null && risk.getIsBanned() == 1) {
            throw new RuntimeException("操作受限：您的账号因违规已被封禁");
        }

        // 存储信息差详细
        OshInfoGap entity = new OshInfoGap();
        entity.setUserId(userId);
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setTag(dto.getTag());
        entity.setStatus(0);

        // 手动保存
        infoGapMapper.insertInfoGap(entity);

        Long infoGapId = entity.getId();
        List<Long> tagIds = dto.getTagIds();
        int sort = 1;
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                OshInfoGapTagRel oshInfoGapTagRel = new OshInfoGapTagRel();
                oshInfoGapTagRel.setInfoGapId(infoGapId);
                oshInfoGapTagRel.setGapTagId(tagId);
                oshInfoGapTagRel.setSortNo(sort++);

                infoGapTagRelMapper.insert(oshInfoGapTagRel);

                LambdaUpdateWrapper<OshInfoGapTag> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(OshInfoGapTag::getId, tagId)
                        .setSql("tag_use_count = tag_use_count + 1");

                infoGapTagMapper.update(null, updateWrapper);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void vote(Long userId, Long infoId, Integer type) {
        // 查出该用户对该信息的旧评价记录
        OshInfoGapVote existVote = voteMapper.selectVoteRecord(userId, infoId);

        // 定义当前点击类型的列名
        String currentColumn = getColumnByType(type);

        // 情况1：从未评价 → 新增评价
        if (existVote == null) {
            OshInfoGapVote vote = new OshInfoGapVote();
            vote.setUserId(userId);
            vote.setInfoGapId(infoId);
            vote.setType(type);
            voteMapper.insertVoteRecord(vote);

            if (!type.equals(0)) {
                infoGapMapper.updateCountAtomically(infoId, currentColumn);
            }

            return;
        }

        Integer oldType = existVote.getType();
        if (oldType.equals(type)) {
            // 重复点击同一评价（取消评价）
            voteMapper.cancelVoteRecord(userId, infoId);
            infoGapMapper.decrementCountAtomically(infoId, currentColumn);
            return;
        }

        if (oldType.equals(0)) {
            // 从 0 → 新评价（恢复/重新评价）
            existVote.setType(type);
            voteMapper.updateVoteRecord(existVote);
        } else {
            // 切换评价（如 1 → 2 / 2 → 3）

            // 1. 减去旧类型的计数
            String oldColumn = getColumnByType(oldType);
            infoGapMapper.decrementCountAtomically(infoId, oldColumn);

            // 2. 更新评价记录的类型
            existVote.setType(type);
            voteMapper.updateVoteRecord(existVote);

            // 3. 增加新类型的计数
            infoGapMapper.updateCountAtomically(infoId, currentColumn);
        }
    }

    @Override
    public List<InfoGapVO> recommend() {
        LambdaQueryWrapper<OshInfoGap> queryWrapper = Wrappers.lambdaQuery(OshInfoGap.class)
                .eq(OshInfoGap::getDeleteFlag, 0)
                .orderByDesc(OshInfoGap::getGoodCount)
                .orderByDesc(OshInfoGap::getCollectCount)
                .last("Limit 3");

        List<OshInfoGap> infoGapList = infoGapMapper.selectList(queryWrapper);
        List<InfoGapVO> infoGapVOList = infoGapList.stream().map(infoGap -> {
            InfoGapVO infoGapVO = new InfoGapVO();
            BeanUtils.copyProperties(infoGap, infoGapVO);
            return infoGapVO;
        }).collect(Collectors.toList());

        return infoGapVOList;
    }

    // 辅助方法：抽取列名逻辑，避免代码重复
    private String getColumnByType(Integer type) {
        return (type == 1) ? "good_count" : (type == 2 ? "middle_count" : "bad_count");
    }

    @Override
    public void infoGapCollectAdd(Long userId, String username, Long infoGapId) {
        if (userId == null) {
            throw new ServiceException("请先登录");
        }
        if (infoGapId == null) {
            throw new ServiceException("信息差ID不能为空");
        }

        // 检查当前用户是否已经收藏了目标信息差


        // 记录收藏关系，统计到 osh_info_gap_follow

        // 计算当前信息差被收藏次数


    }

    @Override
    public void infoGapCollectRemove(Long userId, String username, Long infoGapId) {

    }
}