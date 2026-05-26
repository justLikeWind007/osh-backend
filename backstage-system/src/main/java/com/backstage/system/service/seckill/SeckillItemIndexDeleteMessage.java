package com.backstage.system.service.seckill;

/**
 * 秒杀商品明细索引删除消息
 * 删除链路只依赖明细 ID，保持消息体最小化
 */
public class SeckillItemIndexDeleteMessage {

    private String eventType = SeckillItemIndexEventType.SECKILL_ITEM_INDEX_DELETE;

    /** 明细ID */
    private Long id;

    public SeckillItemIndexDeleteMessage() {
    }

    public SeckillItemIndexDeleteMessage(Long id) {
        this.id = id;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
