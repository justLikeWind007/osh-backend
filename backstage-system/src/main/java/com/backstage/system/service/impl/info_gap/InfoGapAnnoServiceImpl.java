package com.backstage.system.service.impl.info_gap;

import com.backstage.system.domain.dto.info_gap.InfoGapAnnoRespDTO;
import com.backstage.system.service.info_gap.InfoGapAnnoService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InfoGapAnnoServiceImpl implements InfoGapAnnoService {

    @Override
    public List<InfoGapAnnoRespDTO> listSystemNotices() {
        return Collections.emptyList();
    }

    @Override
    public List<InfoGapAnnoRespDTO> listUserNotices() {
        return Collections.emptyList();
    }
}
