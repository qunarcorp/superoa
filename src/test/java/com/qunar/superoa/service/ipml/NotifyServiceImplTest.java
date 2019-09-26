package com.qunar.superoa.service.ipml;

import com.qunar.superoa.dao.FlowOrderRepository;
import com.qunar.superoa.dao.NotifyRepository;
import com.qunar.superoa.model.FlowOrder;
import com.qunar.superoa.model.Notify;
import com.qunar.superoa.service.NotifyServiceI;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class NotifyServiceImplTest {

    @Autowired
    private NotifyServiceImpl notifyServiceI;

    @Autowired
    private NotifyRepository notifyRepository;

    @Test
    public void sendNotify() {
    }


    @Test
    public void readNotify() {
    }

    @Test
    public void readNotify1() {
    }

    @Test
    public void sendMail() throws Exception {
        Notify notify = notifyRepository.findById("8addd3d0668ba5f701669aac157c0004").get();
        notifyServiceI.sendMail(notify);
    }

    @Test
    public void sendQtalk() {
        //Notify notify = notifyRepository.findById("8addbc2b66816070016681fce1780011").get();
        Notify notify = notifyRepository.findById("8adda47466f3ebc6016706db93a60047").get();
        //FlowOrder flowOrder = flowOrderRepository.findById(notify.getFlowID()).get();
        notifyServiceI.sendQtalk(notify);
    }

    @Test
    public void sendWebNotify() {
        Notify notify = notifyRepository.findById("8adda47466f3ebc6016706db93a60047").get();
    }
}