package com.backstage.system.exam;

import com.backstage.system.utils.UserContextUtil;
import org.springframework.stereotype.Component;

/**
 * SpEL helper for {@code @PreAuthorize}: grants exam admin APIs when the current
 * user's effective {@code osh_role.level} is in [4, 6] (e.g. manager / core developer / founder),
 * aligned with product rules independent of fine-grained {@code exam:*} rows in DB.
 */
@Component("examLevelAuth")
public class ExamLevelAuthorization {

    public boolean canManageExamByRoleLevel() {
        try {
            Integer lv = UserContextUtil.getCurrentLevel();
            if (lv == null) {
                return false;
            }
            return lv >= 4 && lv <= 6;
        } catch (Exception ignored) {
            return false;
        }
    }
}
