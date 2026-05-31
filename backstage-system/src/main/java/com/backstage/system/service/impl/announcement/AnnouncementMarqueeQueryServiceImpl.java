package com.backstage.system.service.impl.announcement;

import com.backstage.common.enums.AnnouncementChannelEnum;
import com.backstage.common.enums.AnnouncementModuleEnum;
import com.backstage.system.domain.announcement.vo.AnnouncementMarqueeVO;
import com.backstage.system.mapper.announcement.OshAnnouncementMapper;
import com.backstage.system.service.announcement.IAnnouncementMarqueeQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 公告跑马灯通用查询 Service 实现。
 * <p>
 * 复用统一公告表 osh_announcement 对应的 {@link OshAnnouncementMapper}，
 * 模块 / 栏目通过枚举传入、不在查询中写死，任意业务模块共用本实现。
 *
 * @author backstage
 */
@Service
public class AnnouncementMarqueeQueryServiceImpl implements IAnnouncementMarqueeQueryService {

    /**
     * 默认拉取条数，避免请求方传非法值时打满
     */
    private static final int DEFAULT_LIMIT = 10;

    /**
     * 单次返回上限，防御性兜底
     */
    private static final int MAX_LIMIT = 50;

    @Autowired
    private OshAnnouncementMapper announcementMapper;

    @Override
    public List<AnnouncementMarqueeVO> list(AnnouncementModuleEnum module, AnnouncementChannelEnum channel, int limit) {
        int safeLimit = (limit <= 0 || limit > MAX_LIMIT) ? DEFAULT_LIMIT : limit;
        List<AnnouncementMarqueeVO> records = announcementMapper.selectMarqueeByModuleAndChannel(
                module.getCode(), channel.getCode(), safeLimit);
        return records == null ? Collections.emptyList() : records;
    }
}
