package com.backstage.system.service;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.LiveDetailVo;
import com.backstage.system.domain.vo.LiveQueryVo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/8
 * Time: 14:24
 */
public interface ILiveService {
    R<LiveDetailVo> read(Long id, String token);

    List<LiveQueryVo> list();
}
