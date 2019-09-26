package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.superoa.dao.ExtSysFlowModelRepository;
import com.qunar.superoa.dto.ExtSysResultDto;
import com.qunar.superoa.model.ExtSysFlowModel;
import com.qunar.superoa.service.ExtSysFlowModelServiceI;
import com.qunar.superoa.utils.CommonUtil;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 5:42 PM 2019/4/29
 * @Modify by:
 */
@Component
@Slf4j
public class ExtSysFlowModelServiceImpl implements ExtSysFlowModelServiceI {

  @Autowired
  private ExtSysFlowModelRepository extSysFlowModelRepository;

  @Autowired
  private ExtSysUnapproveFlowServiceImpl extSysUnapproveFlowServiceImpl;

  @Override
  public List<ExtSysResultDto> updateExtSysFlowModel(Object dataSource) {
    List<ExtSysResultDto> resultDtoList;
    //审批系统唯一标识
    String processKeys = String.valueOf(CommonUtil.o2m(dataSource).get("processKeys"));
    //待更新流程审批列表
    List extSysFlowModelList = (List) CommonUtil.o2m(dataSource).get("data");
    resultDtoList = updateExtSysFlowModelList(processKeys, extSysFlowModelList);
    return resultDtoList;
  }

  @Override
  public Object deleteExtSysFlowModel(Object dataSource) {
    //结果列表
    List<ExtSysResultDto> resultDtoList;
    //审批系统唯一标识
    String processKeys = String.valueOf(CommonUtil.o2m(dataSource).get("processKeys"));
    //待删除流程审批列表
    List delExtSysFlowModelList = (List) CommonUtil.o2m(dataSource).get("data");
    //删除待审批流程
    resultDtoList = delExtSysFlowModelList(processKeys, delExtSysFlowModelList);
    return resultDtoList;
  }

  @Override
  public Object updatePatchExtSysFlowModel(Object dataSource) {
    //结果列表
    Map<String, List<ExtSysResultDto>> resultMap = Maps.newHashMap();
    List<ExtSysResultDto> delResultDtoList;
    List<ExtSysResultDto> addResultDtoList;
    //审批系统唯一标识
    String processKeys = String.valueOf(CommonUtil.o2m(dataSource).get("processKeys"));
    //待添加流程审批列表
    List addExtSysFlowModelList = (List) CommonUtil.o2m(dataSource).get("add");
    //待删除流程审批列表
    List delExtSysFlowModelList = (List) CommonUtil.o2m(dataSource).get("remove");
    //删除审批列表
    delResultDtoList = delExtSysFlowModelList(processKeys, delExtSysFlowModelList);
    //更新审批列表
    addResultDtoList = updateExtSysFlowModelList(processKeys, addExtSysFlowModelList);
    resultMap.put("add", addResultDtoList);
    resultMap.put("remove", delResultDtoList);
    return resultMap;
  }

  /**
   * 更新发起流程列表
   * @param processKeys 集成系统唯一标识
   * @param updateExtSysFlowModelList 发起流程列表
   * @return 结果集
   */
  private List<ExtSysResultDto> updateExtSysFlowModelList(String processKeys, List updateExtSysFlowModelList) {
    List<ExtSysResultDto> resultDtoList = Lists.newArrayList();
    if (updateExtSysFlowModelList.size() <= 0) {
      return extSysUnapproveFlowServiceImpl.getNullResult(resultDtoList, "add");
    }
    updateExtSysFlowModelList.forEach(dataItem -> {
      try {
        ExtSysFlowModel extSysFlowModel = getExtSysFlowModel(processKeys, dataItem);
        ExtSysResultDto extSysResultDto = new ExtSysResultDto();
        if (StringUtils.isBlank(extSysFlowModel.getRedirectUrl()) || StringUtils.isBlank(extSysFlowModel.getProcessKeys())) {
          extSysResultDto.setOk(false);
          extSysResultDto.setOperation("add");
          extSysResultDto.setErrorFlow(String.valueOf(CommonUtil.o2m(dataItem).get("oid")));
          extSysResultDto.setMsg("跳转链接或系统标识字段为空");
        } else {
          extSysFlowModelRepository.save(extSysFlowModel);
          extSysResultDto.setOk(true);
          extSysResultDto.setOperation("add");
          extSysResultDto.setMsg(String.valueOf(CommonUtil.o2m(dataItem).get("oid")));
        }
        resultDtoList.add(extSysResultDto);
      } catch (Exception e) {
        ExtSysResultDto unapproveResultDto = extSysUnapproveFlowServiceImpl.getUnapproveExceptionResult(dataItem, e, "add");
        resultDtoList.add(unapproveResultDto);
      }
    });
    return resultDtoList;
  }

  /**
   * 删除可发起流程列表
   * @param processKeys 集成系统唯一标识
   * @param delExtSysFlowModelList 待删除审批流程列表
   * @return 结果集
   */
  private List<ExtSysResultDto> delExtSysFlowModelList(String processKeys, List delExtSysFlowModelList) {
    List<ExtSysResultDto> resultDtoList = Lists.newArrayList();
    if (delExtSysFlowModelList.size() <= 0) {
      return extSysUnapproveFlowServiceImpl.getNullResult(resultDtoList, "remove");
    }
    delExtSysFlowModelList.forEach(dataItem -> {
      try {
        Map<String, Object> unApproveFlowMap = CommonUtil.o2m(dataItem);
        String oid = String.valueOf(unApproveFlowMap.get("oid"));
        List<ExtSysFlowModel> extSysFlowModelList = extSysFlowModelRepository.findByOidAndAndProcessKeys(oid, processKeys);
        if (extSysFlowModelList.size() > 0) {
          extSysFlowModelRepository.deleteAll(extSysFlowModelList);
        }
        ExtSysResultDto extSysResultDto = new ExtSysResultDto();
        extSysResultDto.setOk(true);
        extSysResultDto.setOperation("remove");
        extSysResultDto.setMsg(oid);
        resultDtoList.add(extSysResultDto);
      } catch (Exception e) {
        ExtSysResultDto unapproveResultDto = extSysUnapproveFlowServiceImpl.getUnapproveExceptionResult(dataItem, e, "remove");
        resultDtoList.add(unapproveResultDto);
      }
    });
    return resultDtoList;
  }

  /**
   * 根据参数获取发起流程
   * @param processKeys 集成系统唯一标识
   * @param dataItem 单条参数数据
   * @return 获取一条可发起审批流程
   */
  private ExtSysFlowModel getExtSysFlowModel(String processKeys, Object dataItem) {
    Map<String, Object> unApproveFlowMap = CommonUtil.o2m(dataItem);
    String oid = String.valueOf(unApproveFlowMap.get("oid"));
    //新建流程审批对象
    ExtSysFlowModel extSysFlowModel = new ExtSysFlowModel();
    extSysFlowModel.setProcessKeys(processKeys);
    extSysFlowModel.setOid(String.valueOf(unApproveFlowMap.get("oid")));
    extSysFlowModel.setTitle(String.valueOf(unApproveFlowMap.get("title")));
    extSysFlowModel.setRedirectUrl(String.valueOf(unApproveFlowMap.get("redirectUrl")));
    extSysFlowModel.setFormMark("extSys");

    List<ExtSysFlowModel> extSysFlowModelList = extSysFlowModelRepository.findByOidAndAndProcessKeys(oid, processKeys);
    if (extSysFlowModelList.size() > 1) {
      extSysFlowModelRepository.deleteAll(extSysFlowModelList);
    } else if (extSysFlowModelList.size() == 1) {
      extSysFlowModel.setId(extSysFlowModelList.get(0).getId());
    }
    return extSysFlowModel;
  }

  @Override
  public List<String> getExtSysFlowModelProcessKeys() {
    return extSysFlowModelRepository.findAllProcessKeys();
  }

  @Override
  public List<ExtSysFlowModel> getAllExtSysFlowModel() {
    return extSysFlowModelRepository.findAll();
  }
}
