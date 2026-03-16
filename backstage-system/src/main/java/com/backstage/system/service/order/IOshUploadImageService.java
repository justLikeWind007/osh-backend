package com.backstage.system.service.order;

import java.util.List;
import com.backstage.system.domain.order.OshUploadImage;

/**
 * 图片上传记录Service接口
 * 
 * @author ruoyi
 * @date 2026-03-11
 */
public interface IOshUploadImageService 
{
    /**
     * 查询图片上传记录
     * 
     * @param id 图片上传记录主键
     * @return 图片上传记录
     */
    public OshUploadImage selectOshUploadImageById(Long id);

    /**
     * 查询图片上传记录列表
     * 
     * @param oshUploadImage 图片上传记录
     * @return 图片上传记录集合
     */
    public List<OshUploadImage> selectOshUploadImageList(OshUploadImage oshUploadImage);

    /**
     * 新增图片上传记录
     * 
     * @param oshUploadImage 图片上传记录
     * @return 结果
     */
    public int insertOshUploadImage(OshUploadImage oshUploadImage);

    /**
     * 修改图片上传记录
     * 
     * @param oshUploadImage 图片上传记录
     * @return 结果
     */
    public int updateOshUploadImage(OshUploadImage oshUploadImage);

    /**
     * 批量删除图片上传记录
     * 
     * @param ids 需要删除的图片上传记录主键集合
     * @return 结果
     */
    public int deleteOshUploadImageByIds(Long[] ids);

    /**
     * 删除图片上传记录信息
     * 
     * @param id 图片上传记录主键
     * @return 结果
     */
    public int deleteOshUploadImageById(Long id);
}
