package com.backstage.system.service.announcement;

import com.backstage.system.domain.vo.announcement.ToolAnnouncementVO;
import com.backstage.system.mapper.announcement.OshAnnouncementMapper;
import com.backstage.system.service.impl.announcement.ToolAnnouncementServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToolAnnouncementServiceImplTest {

    @InjectMocks
    private ToolAnnouncementServiceImpl toolAnnouncementService;

    @Mock
    private OshAnnouncementMapper oshAnnouncementMapper;

    @Test
    public void shouldReturnLatestToolAnnouncementsWhenMapperReturnsRows() {
        ToolAnnouncementVO first = new ToolAnnouncementVO();
        first.setId(2L);
        first.setTitle("工具公告2");
        first.setCreateTime(LocalDateTime.of(2026, 5, 19, 10, 0, 0));

        ToolAnnouncementVO second = new ToolAnnouncementVO();
        second.setId(1L);
        second.setTitle("工具公告1");
        second.setCreateTime(LocalDateTime.of(2026, 5, 18, 10, 0, 0));

        when(oshAnnouncementMapper.selectLatestToolAnnouncements()).thenReturn(Arrays.asList(first, second));

        List<ToolAnnouncementVO> result = toolAnnouncementService.listLatestToolAnnouncements();

        assertEquals(2, result.size());
        assertEquals(Long.valueOf(2L), result.get(0).getId());
        assertEquals("工具公告2", result.get(0).getTitle());
        verify(oshAnnouncementMapper).selectLatestToolAnnouncements();
    }

    @Test
    public void shouldReturnEmptyListWhenMapperReturnsNull() {
        when(oshAnnouncementMapper.selectLatestToolAnnouncements()).thenReturn(null);

        List<ToolAnnouncementVO> result = toolAnnouncementService.listLatestToolAnnouncements();

        assertTrue(result.isEmpty());
        verify(oshAnnouncementMapper).selectLatestToolAnnouncements();
    }

    @Test
    public void shouldKeepMapperOrderForLatestFiveToolAnnouncements() {
        when(oshAnnouncementMapper.selectLatestToolAnnouncements()).thenReturn(Collections.emptyList());

        List<ToolAnnouncementVO> result = toolAnnouncementService.listLatestToolAnnouncements();

        assertEquals(0, result.size());
        verify(oshAnnouncementMapper).selectLatestToolAnnouncements();
    }
}
