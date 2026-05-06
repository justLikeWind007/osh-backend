package com.backstage.system.service.tool;

import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolCollection;
import com.backstage.system.mapper.tool.OshToolCollectionMapper;
import com.backstage.system.mapper.tool.OshToolMapper;
import com.backstage.system.service.impl.tool.OshToolCollectionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OshToolCollectionServiceImplTest {

    @InjectMocks
    private OshToolCollectionServiceImpl collectionService;

    @Mock
    private OshToolCollectionMapper oshToolCollectionMapper;

    @Mock
    private OshToolMapper oshToolMapper;

    @Test
    public void shouldIncreaseToolCollectionCountWhenUserCollectsToolFirstTime() {
        when(oshToolMapper.selectToolById(10001L)).thenReturn(buildTool());
        when(oshToolCollectionMapper.selectByUserIdAndToolId(9L, 10001L)).thenReturn(null);
        when(oshToolCollectionMapper.insertToolCollection(org.mockito.ArgumentMatchers.any(OshToolCollection.class))).thenReturn(1);

        collectionService.collectTool(9L, "admin", 10001L);

        verify(oshToolMapper).increaseCollectionCount(10001L);
    }

    @Test
    public void shouldIncreaseToolCollectionCountWhenUserRestoresDeletedCollection() {
        OshToolCollection collection = new OshToolCollection();
        collection.setId(88L);
        collection.setDeleteFlag(1);
        when(oshToolMapper.selectToolById(10001L)).thenReturn(buildTool());
        when(oshToolCollectionMapper.selectByUserIdAndToolId(9L, 10001L)).thenReturn(collection);
        when(oshToolCollectionMapper.updateCollectionDeleteFlag(88L, 0, "admin")).thenReturn(1);

        collectionService.collectTool(9L, "admin", 10001L);

        verify(oshToolMapper).increaseCollectionCount(10001L);
    }

    @Test
    public void shouldNotIncreaseToolCollectionCountWhenUserAlreadyCollectedTool() {
        OshToolCollection collection = new OshToolCollection();
        collection.setId(88L);
        collection.setDeleteFlag(0);
        when(oshToolMapper.selectToolById(10001L)).thenReturn(buildTool());
        when(oshToolCollectionMapper.selectByUserIdAndToolId(9L, 10001L)).thenReturn(collection);

        collectionService.collectTool(9L, "admin", 10001L);

        verify(oshToolMapper, never()).increaseCollectionCount(10001L);
    }

    @Test
    public void shouldDecreaseToolCollectionCountWhenUserCancelsActiveCollection() {
        OshToolCollection collection = new OshToolCollection();
        collection.setId(88L);
        collection.setDeleteFlag(0);
        when(oshToolCollectionMapper.selectByUserIdAndToolId(9L, 10001L)).thenReturn(collection);
        when(oshToolCollectionMapper.updateCollectionDeleteFlag(88L, 1, "admin")).thenReturn(1);

        collectionService.removeToolCollection(9L, "admin", 10001L);

        verify(oshToolMapper).decreaseCollectionCount(10001L);
    }

    @Test
    public void shouldNotDecreaseToolCollectionCountWhenCollectionIsAlreadyRemoved() {
        OshToolCollection collection = new OshToolCollection();
        collection.setId(88L);
        collection.setDeleteFlag(1);
        when(oshToolCollectionMapper.selectByUserIdAndToolId(9L, 10001L)).thenReturn(collection);

        collectionService.removeToolCollection(9L, "admin", 10001L);

        verify(oshToolMapper, never()).decreaseCollectionCount(10001L);
    }

    private OshTool buildTool() {
        OshTool tool = new OshTool();
        tool.setId(10001L);
        return tool;
    }
}
