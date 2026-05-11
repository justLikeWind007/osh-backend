package com.backstage.system.service.impl.tool;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolCollection;
import com.backstage.system.mapper.tool.OshToolCollectionMapper;
import com.backstage.system.mapper.tool.OshToolMapper;
import com.backstage.system.service.tool.IOshToolCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OshToolCollectionServiceImpl implements IOshToolCollectionService {

    @Autowired
    private OshToolCollectionMapper oshToolCollectionMapper;

    @Autowired
    private OshToolMapper oshToolMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectTool(Long userId, String operator, Long toolId) {
        validateToolExists(toolId);
        OshToolCollection collection = oshToolCollectionMapper.selectByUserIdAndToolId(userId, toolId);
        if (collection == null) {
            OshToolCollection newCollection = new OshToolCollection();
            newCollection.setUserId(userId);
            newCollection.setToolId(toolId);
            newCollection.setDeleteFlag(0);
            newCollection.setCreateBy(operator);
            newCollection.setUpdateBy(operator);
            if (oshToolCollectionMapper.insertToolCollection(newCollection) <= 0) {
                throw new ServiceException("收藏工具失败");
            }
            oshToolMapper.increaseCollectionCount(toolId);
            return;
        }
        if (Integer.valueOf(1).equals(collection.getDeleteFlag())) {
            if (oshToolCollectionMapper.updateCollectionDeleteFlag(collection.getId(), 0, operator) <= 0) {
                throw new ServiceException("恢复工具收藏失败");
            }
            oshToolMapper.increaseCollectionCount(toolId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeToolCollection(Long userId, String operator, Long toolId) {
        OshToolCollection collection = oshToolCollectionMapper.selectByUserIdAndToolId(userId, toolId);
        if (collection == null || !Integer.valueOf(0).equals(collection.getDeleteFlag())) {
            return;
        }
        if (oshToolCollectionMapper.updateCollectionDeleteFlag(collection.getId(), 1, operator) <= 0) {
            throw new ServiceException("取消收藏工具失败");
        }
        oshToolMapper.decreaseCollectionCount(toolId);
    }

    private void validateToolExists(Long toolId) {
        OshTool tool = oshToolMapper.selectToolById(toolId);
        if (tool == null) {
            throw new ServiceException("工具不存在");
        }
    }
}
