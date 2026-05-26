package com.backstage.system.service.tool;

import com.backstage.system.domain.tool.OshToolPurchaseRecord;

public interface ToolPurchaseAnnouncementPublisher {

    void publishPurchaseSuccess(OshToolPurchaseRecord record);
}
