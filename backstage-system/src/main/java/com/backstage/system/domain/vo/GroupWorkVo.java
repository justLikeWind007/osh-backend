package com.backstage.system.domain.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/7
 * Time: 15:53
 */
public class GroupWorkVo {
    private Long id;              // 组团ID
    private Integer num;          // 已参团人数
    private Integer total;        // 成团所需总人数
    private LocalDateTime expire;       // 剩余过期时间（小时）
    private LocalDateTime createdTime;  // 创建时间
    private List<GroupUserVo> users; // 已参团的用户列表

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public LocalDateTime getExpire() {
        return expire;
    }

    public void setExpire(LocalDateTime expire) {
        this.expire = expire;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public List<GroupUserVo> getUsers() {
        return users;
    }

    public void setUsers(List<GroupUserVo> users) {
        this.users = users;
    }

//    @Override
//    public String toString() {
//        return "GroupWorkVo{" +
//                "id=" + id +
//                ", num=" + num +
//                ", total=" + total +
//                ", expire=" + expire +
//                ", created_time='" + createdTime + '\'' +
//                ", users=" + users +
//                '}';
//    }
}
