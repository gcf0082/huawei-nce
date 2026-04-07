package com.huawei.nce.business.repository;

import com.huawei.nce.business.model.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByOrderByAlarmTimeDesc();
    List<Alarm> findByHandledFalseOrderByAlarmTimeDesc();
}
