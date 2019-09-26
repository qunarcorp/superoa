package com.qunar.superoa.service.ipml;

import com.google.gson.Gson;
import com.qunar.superoa.dao.FormDataRepository;
import com.qunar.superoa.dto.FormDataDto;
import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.model.FormData;
import com.qunar.superoa.service.FormDataServiceI;
import com.qunar.superoa.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xing.zhou on 2018/8/30.
 */
@Service
public class FormDataServiceIpml implements FormDataServiceI {

    @Autowired
    private FormDataRepository formDataRepository;

    @Override
    public FormData findByProcInstId(String procInstId) {
        return formDataRepository.findByProcInstId(procInstId);
    }

    @Override
    public FormDataDto getFromDataByProcInstId(String procInstId) {
        FormData formData = formDataRepository.findByProcInstId(procInstId);
        FormDataDto formDataDto = new FormDataDto();
        if (formData != null) {
            formDataDto.setProcInstId(formData.getProcInstId());
            formDataDto.setFlowKey(formData.getFormModelFlowKey());
            formDataDto.setFlowName(formData.getFormModelName());
            formDataDto.setFormDatas(formData.getFormDatas());
        }
        return formDataDto;
    }

    @Override
    public FormData addFromData(FormDataDto formDataDto, FlowModel flowModel, boolean update) {
        FormData formData = new FormData();
        formData.setUpdateTime(DateTimeUtil.getDateTime());
        formData.setFormDatas(new Gson().toJson(formDataDto.getFormDatas()));
        formData.setProcInstId(formDataDto.getProcInstId());
        formData.setFormModelFlowKey(formDataDto.getFlowKey());
        formData.setFormModelId(flowModel.getId());
        formData.setFormModelName(flowModel.getFormName());
        formData.setFormVersion(flowModel.getFormVersion().toString());
        if (update) {
            formData.setId(formDataRepository.findByProcInstId(formDataDto.getProcInstId()).getId());
        }
        return formDataRepository.save(formData);
    }

    @Override
    @Transactional
    public FormData updateFromData(String procInstId, String formDatas) {
        FormData formData = formDataRepository.findByProcInstId(procInstId);
        formData.setFormDatas(formDatas);
        return formDataRepository.save(formData);
    }
}
