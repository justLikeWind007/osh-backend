package com.backstage.system.service.impl.common;

import com.backstage.common.utils.DateUtils;
import com.backstage.system.mapper.common.OssMapper;
import com.backstage.system.service.common.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.backstage.common.utils.OssUtil;

@Service
public class OssImpl implements OssService {

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private OssMapper ossMapper;


    public String addCommonAvaterImage(MultipartFile file, String customPath) throws Exception {
        // 获取当前月份
        String month = DateUtils.getDate().substring(5,7);
        customPath = "common/image/avatar/"+month+"/";
        if(file.getSize() > 1024 * 1024 * 3){
            return "图片大小不能超过3M";
        }



        return ossUtil.uploadFile(file, customPath);
    }



}
