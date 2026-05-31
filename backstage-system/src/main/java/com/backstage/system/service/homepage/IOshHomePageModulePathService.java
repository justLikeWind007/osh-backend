package com.backstage.system.service.homepage;

import java.util.Map;

/**
 * 首页模块路径 Service 接口
 * <p>
 * 统一管理各业务模块的前端详情页路径前缀和列表页路径。
 * 各 ServiceImpl 通过模块名（module key）获取对应路径，
 * 避免在每个 ServiceImpl 中硬编码字符串。
 * <p>
 * 模块 key 命名规范：与前端 getNavPath(key) 保持一致。
 *
 * @author jayTatum
 */
public interface IOshHomePageModulePathService {

    /**
     * 获取指定模块的详情页完整路径
     *
     * @param module 模块名（如 "course"、"book"）
     * @param id     资源 ID
     * @return 详情页路径，如 /detail/course/123；模块不存在时返回 "/"
     */
    String getDetailPath(String module, Object id);

    /**
     * 获取指定模块的列表页路径
     *
     * @param module 模块名（如 "course"、"book"）
     * @return 列表页路径；模块不存在时返回 "/"
     */
    String getListPath(String module);

    /**
     * 获取所有模块的列表页路径映射（供 NavController 使用）
     *
     * @return 模块名 → 列表页路径 的不可变视图
     */
    Map<String, String> getAllListPaths();
}
