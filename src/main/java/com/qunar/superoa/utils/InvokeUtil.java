package com.qunar.superoa.utils;

import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.InvokeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/10_下午6:10
 * @Despriction: 反射工具类
 */


public class InvokeUtil<T> {

    private final static Logger logger = LoggerFactory.getLogger(InvokeUtil.class);

    public static Object newDto(Object model, Class<?> dtoClass)  {
        logger.info("model: ${}  dto: ${}", model, dtoClass);
        try {
            return dtoClass.getConstructor(Object.class).newInstance(model);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new InvokeException(ResultEnum.INVOKE_ERROR);
    }
}
