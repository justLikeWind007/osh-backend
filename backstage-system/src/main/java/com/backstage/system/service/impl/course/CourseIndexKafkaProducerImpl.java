package com.backstage.system.service.impl.course;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.constant.KafkaConstants;
import com.backstage.common.utils.kafka.KafkaMessageUtil;
import com.backstage.system.service.course.CourseIndexKafkaProducer;
import com.backstage.system.service.course.CourseIndexUpsertMessage;
import org.springframework.stereotype.Service;

@Service
public class CourseIndexKafkaProducerImpl implements CourseIndexKafkaProducer {

    @Override
    public void sendCourseIndexCreate(CourseIndexUpsertMessage message) {
        KafkaMessageUtil.sendMessage(KafkaConstants.COURSE_INDEX_TOPIC, JSON.toJSONString(message));
    }

    @Override
    public void sendCourseIndexUpdate(CourseIndexUpsertMessage message) {
        KafkaMessageUtil.sendMessage(KafkaConstants.COURSE_INDEX_TOPIC, JSON.toJSONString(message));
    }
}
