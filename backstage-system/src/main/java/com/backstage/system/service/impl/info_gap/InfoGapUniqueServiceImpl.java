package com.backstage.system.service.impl.info_gap;

import com.backstage.system.domain.info_gap.OshInfoGapUnique;
import com.backstage.system.mapper.info_gap.OshInfoGapUniqueMapper;
import com.backstage.system.service.info_gap.InfoGapUniqueService;
import com.backstage.system.utils.InfoGapUniqueUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InfoGapUniqueServiceImpl implements InfoGapUniqueService {

    private static final int MAX_RETRY_TIMES = 10;

    private final OshInfoGapUniqueMapper oshInfoGapUniqueMapper;

    public InfoGapUniqueServiceImpl(OshInfoGapUniqueMapper oshInfoGapUniqueMapper) {
        this.oshInfoGapUniqueMapper = oshInfoGapUniqueMapper;
    }

    @Override
    public String generateUniqueNo() {
        for (int i = 0; i < MAX_RETRY_TIMES; i++) {
            String no = InfoGapUniqueUtil.generate();
            Long count = oshInfoGapUniqueMapper.selectCount(
                    new LambdaQueryWrapper<OshInfoGapUnique>()
                            .eq(OshInfoGapUnique::getNo, no)
            );
            if (count == null || count == 0L) {
                return no;
            }
        }
        throw new RuntimeException("生成唯一信息差标签失败，请稍后重试");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createUniqueRecord(Long infoGapId) {
        if (infoGapId == null) {
            throw new RuntimeException("信息差ID不能为空");
        }

        String no = generateUniqueNo();

        OshInfoGapUnique entity = new OshInfoGapUnique();
        entity.setNo(no);
        entity.setInfoGapId(infoGapId);
        entity.setDeleteFlag(0);

        int rows = oshInfoGapUniqueMapper.insert(entity);
        if (rows <= 0) {
            throw new RuntimeException("保存信息差唯一标签失败");
        }

        return no;
    }
}
