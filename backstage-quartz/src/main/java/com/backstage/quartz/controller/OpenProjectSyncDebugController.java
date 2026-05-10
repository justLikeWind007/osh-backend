package com.backstage.quartz.controller;

import com.backstage.common.annotation.Anonymous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 开源项目 GitHub 同步调试接口
 * 仅用于开发调试，方便在 IDE 中打断点验证同步逻辑
 * 上线前可删除此文件
 */
@RestController
@RequestMapping("/debug/openproject")
public class OpenProjectSyncDebugController {

    @Autowired
    private OpenProjectSyncTask openProjectSyncTask;

    /**
     * 触发全量同步
     * GET http://localhost:8081/debug/openproject/sync-all
     */
    @Anonymous
    @GetMapping("/sync-all")
    public Map<String, Object> syncAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            openProjectSyncTask.syncAll();
            result.put("success", true);
            result.put("msg", "全量同步完成");
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 触发单个项目同步
     * GET http://localhost:8081/debug/openproject/sync-one?id=1&owner=vuejs&repo=vue
     */
    @GetMapping("/sync-one")
    @Anonymous
    public Map<String, Object> syncOne(
            @RequestParam Long id,
            @RequestParam String owner,
            @RequestParam String repo) {
        Map<String, Object> result = new HashMap<>();
        try {
            openProjectSyncTask.syncOne(id, owner, repo);
            result.put("success", true);
            result.put("msg", "同步完成，projectId=" + id);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
