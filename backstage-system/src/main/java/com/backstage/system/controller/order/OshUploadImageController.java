package com.backstage.system.controller.order;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.backstage.common.annotation.Anonymous;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.enums.BusinessType;
import com.backstage.system.domain.order.OshUploadImage;
import com.backstage.system.service.order.IOshUploadImageService;
import com.backstage.common.utils.poi.ExcelUtil;
import com.backstage.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import com.backstage.common.exception.file.FileUploadException;
import com.backstage.common.utils.file.OssUtil;

/**
 * 图片上传记录Controller
 * 
 * @author ruoyi
 * @date 2026-03-11
 */
@RestController
@RequestMapping("/pc/upload")
public class OshUploadImageController extends BaseController
{
    @Autowired
    private IOshUploadImageService oshUploadImageService;

    @Autowired
    private OssUtil ossUtil;

    /**
     * 查询图片上传记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:image:list')")
    @GetMapping("/list")
    public TableDataInfo list(OshUploadImage oshUploadImage)
    {
        startPage();
        List<OshUploadImage> list = oshUploadImageService.selectOshUploadImageList(oshUploadImage);
        return getDataTable(list);
    }

    /**
     * 导出图片上传记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:image:export')")
    @Log(title = "图片上传记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OshUploadImage oshUploadImage)
    {
        List<OshUploadImage> list = oshUploadImageService.selectOshUploadImageList(oshUploadImage);
        ExcelUtil<OshUploadImage> util = new ExcelUtil<OshUploadImage>(OshUploadImage.class);
        util.exportExcel(response, list, "图片上传记录数据");
    }

    /**
     * 获取图片上传记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:image:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(oshUploadImageService.selectOshUploadImageById(id));
    }

    /**
     * 上传图片到阿里云 OSS
     */
    @Log(title = "上传图片", businessType = BusinessType.INSERT)
    @Anonymous
    @PostMapping("/image")
    public AjaxResult uploadImage(MultipartFile file) throws Exception
    {
        if (file == null || file.isEmpty()) {
            return error("上传文件不能为空");
        }

        String url = ossUtil.uploadFile(file);

        OshUploadImage uploadImage = new OshUploadImage();
        uploadImage.setUserId(1L);
        uploadImage.setSchoolId(1L);
        uploadImage.setFileName(file.getOriginalFilename());
        uploadImage.setFilePath(url);
        uploadImage.setFileSize(file.getSize());
        uploadImage.setFileType(file.getContentType());
        uploadImage.setStatus(1L);

        int result = oshUploadImageService.insertOshUploadImage(uploadImage);
        
        if (result > 0) {
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", file.getOriginalFilename());
            ajax.put("fileSize", file.getSize());
            return ajax;
        } else {
            return error("上传失败");
        }
    }

    /**
     * 删除图片（同时删除 OSS 上的文件）
     */
    @PreAuthorize("@ss.hasPermi('system:image:remove')")
    @Log(title = "图片上传记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        for (Long id : ids) {
            OshUploadImage uploadImage = oshUploadImageService.selectOshUploadImageById(id);
            if (uploadImage != null && uploadImage.getFilePath() != null) {
                ossUtil.deleteFile(uploadImage.getFilePath());
            }
        }
        
        int result = oshUploadImageService.deleteOshUploadImageByIds(ids);
        return toAjax(result);
    }

    /**
     * 新增图片上传记录
     */
//    @PreAuthorize("@ss.hasPermi('system:image:add')")
    @Anonymous
    @Log(title = "图片上传记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MultipartRequest file)
    {
        //TODO 上传图片
        OshUploadImage oshUploadImage = new OshUploadImage();
        return toAjax(oshUploadImageService.insertOshUploadImage(oshUploadImage));
    }

    /**
     * 修改图片上传记录
     */
    @PreAuthorize("@ss.hasPermi('system:image:edit')")
    @Log(title = "图片上传记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OshUploadImage oshUploadImage)
    {
        return toAjax(oshUploadImageService.updateOshUploadImage(oshUploadImage));
    }
}
