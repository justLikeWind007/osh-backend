package com.backstage.system.service.tool;

public interface IOshToolCollectionService {

    void collectTool(Long userId, String operator, Long toolId);

    void removeToolCollection(Long userId, String operator, Long toolId);
}
