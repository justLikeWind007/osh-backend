package com.backstage.system.service.order.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backstage.system.mapper.common.OshUploadImageMapper;
import com.backstage.system.domain.order.OshUploadImage;
import com.backstage.system.service.order.IOshUploadImageService;

/**
 * 图片上传记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-11
 */
@Service
public class OshUploadImageServiceImpl implements IOshUploadImageService 
{
    @Autowired
    private OshUploadImageMapper oshUploadImageMapper;

    /**
     * 查询图片上传记录
     * 
     * @param id 图片上传记录主键
     * @return 图片上传记录
     */
    @Override
    public OshUploadImage selectOshUploadImageById(Long id)
    {
        return oshUploadImageMapper.selectOshUploadImageById(id);
    }

    /**
     * 查询图片上传记录列表
     * 
     * @param oshUploadImage 图片上传记录
     * @return 图片上传记录
     */
    @Override
    public List<OshUploadImage> selectOshUploadImageList(OshUploadImage oshUploadImage)
    {
        return oshUploadImageMapper.selectOshUploadImageList(oshUploadImage);
    }

    /**
     * 新增图片上传记录
     * 
     * @param oshUploadImage 图片上传记录
     * @return 结果
     */
    @Override
    public int insertOshUploadImage(OshUploadImage oshUploadImage)
    {
        return oshUploadImageMapper.insertOshUploadImage(oshUploadImage);
    }

    /**
     * 修改图片上传记录
     * 
     * @param oshUploadImage 图片上传记录
     * @return 结果
     */
    @Override
    public int updateOshUploadImage(OshUploadImage oshUploadImage)
    {
        return oshUploadImageMapper.updateOshUploadImage(oshUploadImage);
    }

    /**
     * 批量删除图片上传记录
     * 
     * @param ids 需要删除的图片上传记录主键
     * @return 结果
     */
    @Override
    public int deleteOshUploadImageByIds(Long[] ids)
    {
        return oshUploadImageMapper.deleteOshUploadImageByIds(ids);
    }

    /**
     * 删除图片上传记录信息
     * 
     * @param id 图片上传记录主键
     * @return 结果
     */
    @Override
    public int deleteOshUploadImageById(Long id)
    {
        return oshUploadImageMapper.deleteOshUploadImageById(id);
    }
}
