package com.backstage.system.service.impl.tool;

import com.backstage.common.enums.ResourceCodePrefixEnum;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.utils.generate.GenerateUtil;
import com.backstage.system.service.tool.ResourceNoDuplicateChecker;
import com.backstage.system.service.tool.ResourceNoGenerator;
import org.springframework.stereotype.Service;

@Service
public class ResourceNoGeneratorImpl implements ResourceNoGenerator {

    private static final int MAX_RETRY_COUNT = 20;

    @Override
    public String generateUniqueNo(ResourceCodePrefixEnum prefixEnum, ResourceNoDuplicateChecker duplicateChecker) {
        if (prefixEnum == null) {
            throw new IllegalArgumentException("资源编号前缀不能为空");
        }
        if (duplicateChecker == null) {
            throw new IllegalArgumentException("资源编号判重器不能为空");
        }
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            String no = GenerateUtil.generateResourceCode(prefixEnum);
            if (!duplicateChecker.exists(no)) {
                return no;
            }
        }
        throw new ServiceException("生成资源编号失败，请稍后重试");
    }
}
