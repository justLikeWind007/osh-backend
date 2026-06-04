package com.backstage.flink;

import com.bachstage.memory.stress.MemoryStressJobOptions;
import com.bachstage.memory.stress.MemoryStressMode;
import org.apache.flink.api.common.operators.SlotSharingGroup;
import org.junit.Assert;
import org.junit.Test;

/**
 * MemoryStressJobOptions 参数解析测试
 */
public class MemoryStressJobOptionsTest {

    /**
     * 验证 heap 模式可以使用 heapMb 快捷参数同步 taskHeap 与 holdHeap
     */
    @Test
    public void shouldParseHeapModeShortcutParameters() {
        MemoryStressJobOptions options = MemoryStressJobOptions.parse(
                new String[] {"--heapMb", "64", "--parallelism", "2", "--jobName", "heap-64m"},
                MemoryStressMode.HEAP);

        Assert.assertEquals(MemoryStressMode.HEAP, options.getMode());
        Assert.assertEquals(64, options.getHoldHeapMb());
        Assert.assertEquals(64, options.getTaskHeapMb());
        Assert.assertEquals(0, options.getManagedMb());
        Assert.assertEquals(2, options.getParallelism());
        Assert.assertEquals("heap-64m", options.getJobName());
    }

    /**
     * 验证 managed 模式可以正确构造 slot sharing group 资源画像
     */
    @Test
    public void shouldBuildManagedMemorySlotSharingGroup() {
        MemoryStressJobOptions options = MemoryStressJobOptions.parse(
                new String[] {"--managedMb", "128", "--taskHeapMb", "64", "--cpuCores", "0.5"},
                MemoryStressMode.MANAGED);

        SlotSharingGroup slotSharingGroup = options.buildSlotSharingGroup();

        Assert.assertEquals("memory-stress-managed", slotSharingGroup.getName());
        Assert.assertTrue(slotSharingGroup.getCpuCores().isPresent());
        Assert.assertEquals(0.5D, slotSharingGroup.getCpuCores().get().doubleValue(), 0.0001D);
        Assert.assertTrue(slotSharingGroup.getTaskHeapMemory().isPresent());
        Assert.assertEquals(64L * 1024 * 1024, slotSharingGroup.getTaskHeapMemory().get().getBytes());
        Assert.assertTrue(slotSharingGroup.getManagedMemory().isPresent());
        Assert.assertEquals(128L * 1024 * 1024, slotSharingGroup.getManagedMemory().get().getBytes());
    }

    /**
     * 验证 managed 模式下不允许 managedMb 为 0
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectManagedModeWithoutManagedMemory() {
        MemoryStressJobOptions.parse(
                new String[] {"--managedMb", "0", "--taskHeapMb", "64"},
                MemoryStressMode.MANAGED);
    }

    /**
     * 验证 combined 模式支持同时设置 heap 与 managed 内存
     */
    @Test
    public void shouldParseCombinedModeParameters() {
        MemoryStressJobOptions options = MemoryStressJobOptions.parse(
                new String[] {"--holdHeapMb", "96", "--taskHeapMb", "128", "--managedMb", "64"},
                MemoryStressMode.COMBINED);

        Assert.assertEquals(96, options.getHoldHeapMb());
        Assert.assertEquals(128, options.getTaskHeapMb());
        Assert.assertEquals(64, options.getManagedMb());
    }
}
