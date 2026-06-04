package com.backstage.flink;

import org.junit.Assert;
import org.junit.Test;

/**
 * Flink 提交脚本参数示例测试
 */
public class FlinkSubmitScriptArgumentExamplesTest {

    /**
     * 验证并行度参数示例字符串保持预期格式，便于文档与脚本保持一致
     */
    @Test
    public void shouldContainParallelismExample() {
        String command = "./submit_flink_job.sh -c com.bachstage.memory.stress.HeapMemoryHoldJob -p 2 -- --heapMb 64 --parallelism 2";
        Assert.assertTrue(command.contains("-p 2"));
        Assert.assertTrue(command.contains("--parallelism 2"));
    }

    /**
     * 验证 JVM 参数示例字符串保持预期格式，便于文档与脚本保持一致
     */
    @Test
    public void shouldContainJvmOptionsExample() {
        String command = "./submit_flink_job.sh -c com.bachstage.memory.stress.ManagedMemoryProfileJob --tm-jvm-opts \"-Xms256m -Xmx256m\"";
        Assert.assertTrue(command.contains("--tm-jvm-opts"));
        Assert.assertTrue(command.contains("-Xmx256m"));
    }
}
