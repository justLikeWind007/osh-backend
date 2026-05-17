package com.backstage.system.utils;

import com.backstage.common.enums.ResourceTypeEnum;
import com.backstage.common.utils.spring.SpringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/23
 * Time: 18:48
 */
public class ResourcePermissionUtil {
    private static JdbcTemplate getJdbcTemplate() {
        return SpringUtils.getBean(JdbcTemplate.class);
    }

    /**
     * 公共方法：校验用户是否有权限访问指定资源
     *
     * @param resourceTypeEnum 资源类型（course / qa / book ...）
     * @param resourceId   资源ID
     * @return true-有权限，false-无权限
     */
    public static Integer getResourceLevel(ResourceTypeEnum resourceTypeEnum, Long resourceId) {
        String tableName = resourceTypeEnum.getTableName();
        Integer requiredLevel = getRequiredLevelFromTable(tableName, resourceId);
        if (requiredLevel == null) {
            return -1;
        }
        return requiredLevel;
    }

    /**
     * 根据表名和资源ID动态查询 required_level
     */
    private static Integer getRequiredLevelFromTable(String tableName, Long resourceId) {
        String sql = "select level from " + tableName + " where id = ?";
        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class, resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean hasPermission(ResourceTypeEnum resourceTypeEnum, Long resourceId) {
        Integer resourceLevel = ResourcePermissionUtil.getResourceLevel(resourceTypeEnum, resourceId);
        return UserContextUtil.getCurrentLevel() >= resourceLevel;
    }
}
