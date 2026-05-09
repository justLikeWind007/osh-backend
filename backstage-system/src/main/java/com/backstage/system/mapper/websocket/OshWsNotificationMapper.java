package com.backstage.system.mapper.websocket;

import com.backstage.system.domain.websocket.WsNotifyMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OshWsNotificationMapper extends BaseMapper<WsNotifyMessage> {
}
