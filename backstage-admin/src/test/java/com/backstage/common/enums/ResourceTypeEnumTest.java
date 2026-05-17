package com.backstage.common.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResourceTypeEnumTest {

    @Test
    public void shouldResolveToolResourceTypeForQuestionAnswerBinding() {
        ResourceTypeEnum tool = ResourceTypeEnum.fromTypeCode("tool");

        assertEquals(ResourceTypeEnum.TOOL, tool);
        assertEquals("osh_tool", tool.getMysqlTableName());
    }
}
