package org.backstage.hbase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HBaseTest {

//    @Test
    public void test() {
        HBaseClient client = new ThriftV1HBaseClient("43.242.200.25", 9090);

        client.dropTable("user");
        client.createTable(User.class);

        for (int i = 0; i < 30; i++) {
            User user = new User();
            user.setId("u" + i);
            user.setName("用户" + i);
            user.setAge(String.valueOf(i));
            client.save(user);
        }

        User u = client.get(User.class, "u0");
        System.out.println(u);

        List<User> users = client.scan(User.class, "u0", 10);
        for (User user1 : users) {
            System.out.println(user1);
        }
    }

//    @Test
    public void batchTest() {
        HBaseClient client = new ThriftV1HBaseClient("43.242.200.25", 9090);

        Random random = new Random();
        // 1000_0000 条 10 min 左右
        for (int i = 0; i < 10000; i++) {
            List<User> users = new ArrayList<>();
            for (int j = 0; j < 10000; j++) {
                User user = new User();
                user.setId("user" + i + j);
                user.setName("用户" + i + j);
                user.setAge(String.valueOf(random.nextInt(100) + 1));
                users.add(user);
            }

            System.out.println("保存" + 10000 * (i + 1) + "条数据...");
            client.saveBatch(users);
        }

    }

//    @Test
    public void query() {
        HBaseClient client = new ThriftV1HBaseClient("43.242.200.25", 9090);

        // 单条数据等值查询  23846ms
        runWithTimer("单条数据等值查询", () -> {
            List<User> users = client.scanWithFilter(User.class, "SingleColumnValueFilter('info','name',=,'binary:用户9582344')", 10);
            System.out.println(users);
        });

        // 范围查询 578ms
        runWithTimer("范围查询", () -> {
            List<User> users = client.scanWithFilter(User.class, "SingleColumnValueFilter('info','age',>, 'binary:34940')", 10);
            System.out.println(users);
        });
    }

    private static void runWithTimer(String label, Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        System.out.println("[" + label + "]耗时：" + (System.currentTimeMillis() - start) + "ms");
    }
}
