package com.backstage.system.service.tool;

import com.backstage.common.enums.ResourceCodePrefixEnum;
import com.backstage.system.service.impl.tool.ResourceNoGeneratorImpl;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ResourceNoGeneratorImplTest {

    @Test
    public void shouldRetryWhenGeneratedNoAlreadyExistsAndReturnUniqueNo() {
        ResourceNoGeneratorImpl generator = new ResourceNoGeneratorImpl();
        AtomicInteger callCount = new AtomicInteger(0);

        String no = generator.generateUniqueNo(ResourceCodePrefixEnum.TOOL, value -> callCount.getAndIncrement() == 0);

        assertEquals(2, callCount.get());
        assertEquals("tl", no.substring(0, 2));
        assertEquals(8, no.length());
    }
}
