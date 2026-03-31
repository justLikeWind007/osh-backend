package com.backstage.system.service.impl.common;

import com.backstage.common.enums.UploadPathEnum;
import com.backstage.common.utils.DateUtils;
import com.backstage.system.domain.vo.common.OssOperationLogVo;
import com.backstage.system.mapper.common.OssMapper;
import com.backstage.system.service.common.OssService;
import com.backstage.system.utils.OssUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class OssImpl implements OssService {

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private OssMapper ossMapper;


    // TODO

    // enum 固定路径类型 course_code.zip 7z
    public String upload(MultipartFile file, UploadPathEnum pathEnum) throws  Exception{


        String customPath;

        // 获取年月
        String ym = DateUtils.getDate().substring(0,7).replace("-", "");;



        if(UploadPathEnum.AVATAR.equals(pathEnum)){
            customPath = UploadPathEnum.AVATAR.getPath()+ym+"/";
            if(file.getSize() > 1024 * 1024 * 3){
                return "图片大小不能超过3M";
            }

        }else if(UploadPathEnum.COURSE_VIDEO.equals(pathEnum)){
            customPath = UploadPathEnum.COURSE_VIDEO.getPath()+ym+"/";
            if(file.getSize() > 1024 * 1024 * 200){
                return "视频大小不能超过200MB";
            }
        }else {
            return "类型不正确";
        }


        return ossUtil.uploadFile(file, customPath);
    }



    public boolean existsFileKey(String fileKey) {
        if (fileKey == null || fileKey.trim().isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<OssOperationLogVo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OssOperationLogVo::getFileKey, fileKey);
        return ossMapper.exists(wrapper);
    }



    public int incrementOperationCount(String fileKey) {
        if (fileKey == null || fileKey.trim().isEmpty()) {
            return 0;
        }

        // 直接使用 updateWrapper 让 operation_count + 1
        LambdaUpdateWrapper<OssOperationLogVo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OssOperationLogVo::getFileKey, fileKey)
                .setSql("operation_count = operation_count + 1");

        return ossMapper.update(null, updateWrapper);
    }


}
