package com.backstage.system.service.announcement;

import com.backstage.common.enums.AnnouncementChannelEnum;
import com.backstage.common.enums.AnnouncementModuleEnum;
import com.backstage.system.domain.announcement.vo.AnnouncementMarqueeVO;

import java.util.List;

/**
 * 公告跑马灯通用查询 Service。
 * <p>
 * 面向统一公告表 osh_announcement 的展示型查询，按「模块 + 栏目」维度取数，
 * 不与任何单一业务模块绑定：反馈、（未来）首页等任意"跑马灯式公告"场景均通过
 * 传入 {@link AnnouncementModuleEnum} 复用同一入口，避免每模块复制一份 Service。
 *
 * @author backstage
 */
public interface IAnnouncementMarqueeQueryService {

    /**
     * 按模块 + 栏目查询公告跑马灯条目。
     *
     * @param module  归属模块
     * @param channel 栏目（公告 / 动态）
     * @param limit   返回条数上限（非法值由实现兜底）
     * @return 公告跑马灯条目，按 sort、create_time 倒序
     */
    List<AnnouncementMarqueeVO> list(AnnouncementModuleEnum module, AnnouncementChannelEnum channel, int limit);
}
