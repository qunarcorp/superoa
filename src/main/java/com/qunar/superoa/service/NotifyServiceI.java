package com.qunar.superoa.service;

import com.qunar.superoa.dto.NotifyDto;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.model.Notify;
import com.qunar.superoa.dto.PageAble;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/11_下午9:08
 * @Despriction: 通知服务接口
 */

public interface NotifyServiceI {

    /**
     * 发送通知
     * @param notify
     */
    void sendNotify(Notify notify);

    /**
     * 发送通知
     * @param notify
     * @param qtalks (qtalk账号逗号分隔)
     */
    void sendNotify(Notify notify, String qtalks);

    /**
     * 批量清除读消息
     * @param ids
     */
    void readNotify(String ids);

    /**
     * 获取当前用户通知
     * @param pageAble
     * @return
     */
    PageResult<NotifyDto> findAll(PageAble pageAble);

    /**
     * 获取所有用户通知
     * @param pageAble
     * @return
     */
    PageResult<NotifyDto> findCurrentUserNotify(PageAble pageAble);

    /**
     * 获取所有用户通知
     * @param pageAble
     * @return
     */
    PageResult<NotifyDto> findUnread(PageAble pageAble);
}
