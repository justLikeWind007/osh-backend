package com.backstage.system.service.impl.info_gap;
import com.backstage.system.domain.dto.info_gap.InfoGapCreateDTO;
import com.backstage.system.domain.info_gap.OshInfoGap;
import com.backstage.system.domain.info_gap.OshInfoGapFollow;
import com.backstage.system.domain.info_gap.OshInfoGapVote;
import com.backstage.system.domain.user.risk.OshUserRiskProfile;
import com.backstage.system.domain.vo.info_gap.InfoGapVO;
import com.backstage.system.mapper.info_gap.OshInfoGapFollowMapper;
import com.backstage.system.mapper.info_gap.OshInfoGapMapper;
import com.backstage.system.mapper.info_gap.OshInfoGapVoteMapper;
import com.backstage.system.mapper.info_gap.OshUserRiskProfileMapper;
import com.backstage.system.service.info_gap.InfoGapService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Page<InfoGapVO> getInfoGapList(Integer pageNum, String type, Long currentUserId) {
        Page<InfoGapVO> page = new Page<>(pageNum, 10);
        return infoGapMapper.selectInfoGapPage(page, type, currentUserId);
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

        OshInfoGap entity = new OshInfoGap();
        entity.setUserId(userId);
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setTag(dto.getTag());
        entity.setStatus(0);

        // 手动保存
        infoGapMapper.insertInfoGap(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void vote(Long userId, Long infoId, Integer type) {
        // 1. 查出该用户对该信息的旧评价记录
        OshInfoGapVote existVote = voteMapper.selectVoteRecord(userId, infoId);

        // 定义当前点击类型的列名
        String currentColumn = getColumnByType(type);

        if (existVote != null) {
            // 情况 A：点击的是同一种类型 -> 取消评价（逻辑不变）
            if (existVote.getType().equals(type)) {
                voteMapper.deleteVoteRecord(existVote.getId());
                infoGapMapper.decrementCountAtomically(infoId, currentColumn);
                return;
            } else {
                // 情况 B：【核心修改】切换评价（比如从好评 1 切换到差评 3）
                // 1. 减去旧类型的计数
                String oldColumn = getColumnByType(existVote.getType());
                infoGapMapper.decrementCountAtomically(infoId, oldColumn);

                // 2. 更新评价记录的类型
                existVote.setType(type);
                voteMapper.updateVoteRecord(existVote); // 这里需要你 Mapper 有个 update 方法

                // 3. 增加新类型的计数
                infoGapMapper.updateCountAtomically(infoId, currentColumn);
                return;
            }
        }

        // 2. 情况 C：不存在评价，直接新增（逻辑不变）
        OshInfoGapVote vote = new OshInfoGapVote();
        vote.setUserId(userId);
        vote.setInfoGapId(infoId);
        vote.setType(type);

        voteMapper.insertVoteRecord(vote);
        infoGapMapper.updateCountAtomically(infoId, currentColumn);
    }

    // 辅助方法：抽取列名逻辑，避免代码重复
    private String getColumnByType(Integer type) {
        return (type == 1) ? "good_count" : (type == 2 ? "middle_count" : "bad_count");
    }
}