package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.DepartmentRepository;
import com.qunar.superoa.dao.FlowModelRepository;
import com.qunar.superoa.dao.FlowOrderRepository;
import com.qunar.superoa.dto.DeptDto;
import com.qunar.superoa.dto.FlowModelDto;
import com.qunar.superoa.dto.FlowModelNameDto;
import com.qunar.superoa.dto.FlowStatusDto;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.FlowModelException;
import com.qunar.superoa.model.FlowModel;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.FlowModelServiceI;
import com.qunar.superoa.utils.CommonUtil;
import com.qunar.superoa.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.Predicate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


/**
 * @Auther: xing.zhou
 * @Auther: lee.guo
 * @Date:Created in 2018/9/27_9:54 PM
 * @Despriction:
 */

@Service
@Slf4j
public class FlowModelServiceImpl implements FlowModelServiceI {

  @Autowired
  private FlowModelRepository flowModelRepository;

  @Autowired
  private FlowOrderRepository flowOrderRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  /**
   * 去重
   */
  private void addFlowModel(List<FlowModelNameDto> flowModelNames, Map<String, String> flowNames,
                            FlowModel flowModel) {
    if (flowNames.get(flowModel.getFormKey()) == null || flowNames.get(flowModel.getFormKey())
        .isEmpty()) {
      FlowModelNameDto flowModelNameDto = new FlowModelNameDto();
      flowModelNameDto.setFormKey(flowModel.getFormKey());
      flowModelNameDto.setFormName(flowModel.getFormName());
      flowModelNameDto.setFormMark("qoa");
      flowNames.put(flowModel.getFormKey(), flowModel.getFormName());
      flowModelNames.add(flowModelNameDto);
    }
  }

  /**
   * Dto转Model
   *
   * @param flowModelDto FlowModelDto对象
   * @return FlowModel对象
   */
  private FlowModel flowModelDtoToModel(FlowModelDto flowModelDto) {
    FlowModel flowModel = new FlowModel();
    flowModel.setFormDept(flowModelDto.getFormDept());
    flowModel.setFormKey(flowModelDto.getFormKey());
    flowModel.setFormName(flowModelDto.getFormName());
    flowModel.setFormModels(new Gson().toJson(flowModelDto.getFormModelJson()));
    flowModel.setUpdateTime(DateTimeUtil.getDateTime());
    flowModel.setFlowStatus(flowModelDto.getFlowStatus());
    return flowModel;
  }


  /**
   * Model转Dto
   *
   * @param flowModel FlowModel对象
   * @return FlowModelDto对象
   */
  private FlowModelDto flowModelToDto(FlowModel flowModel) {
    if (flowModel == null) {
      return null;
    }
    FlowModelDto flowModelDto = new FlowModelDto();
    flowModelDto.setFormDept(flowModel.getFormDept());
    flowModelDto.setFormName(flowModel.getFormName());
    flowModelDto.setFormKey(flowModel.getFormKey());
    flowModelDto.setFormModelJson(flowModel.getFormModels());
    flowModelDto.setFlowStatus(flowModel.getFlowStatus());
    flowModelDto.setFormBranchConditions(flowModel.getFormBranchConditions());
    return flowModelDto;
  }


  /**
   * 添加表单
   */
  @Override
  public FlowModelDto addFlowMode(FlowModelDto flowModelDto) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    //判断表单FormKey，FormName和FormDept是否为null或空串或由空白符(whitespace)构成
    if (StringUtils.isBlank(flowModelDto.getFormKey())) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_KEY_NULL);
    }
    if (StringUtils.isBlank(flowModelDto.getFormName())) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_NAME_NULL);
    }
    if (StringUtils.isBlank(flowModelDto.getFormDept())) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_DEPT_NULL);
    }

    //判断表单FormKey以及FormName是否已存在数据库中
    if (getFlowModelDtoByFormKey(flowModelDto.getFormKey()) != null) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_KEY);
    } else if (getFlowModelByFormName(flowModelDto.getFormName()) != null) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_NAME);
    }
    flowModelDto.setFlowStatus(Constant.FLOW_UN_START_USED);
    FlowModel flowModel = flowModelDtoToModel(flowModelDto);
    //获取表单模版相关的属性，包括节点分支条件、可编辑节点
    Map formProperties = getFormProperties(flowModelDto.getFormModelJson());
    //设置模版节点分支条件
    flowModel.setFormBranchConditions(String.valueOf(formProperties.get("formBranchConditions")));
    flowModel.setFormVersion(1);
    flowModel.setFormKey(flowModel.getFormKey());
    return flowModelToDto(flowModelRepository.save(flowModel));
  }

  /**
   * 根据模版内容获取模版相关属性
   *
   * @param formModelJson 流程模版Map
   * @return 模版相关属性
   */
  private Map getFormProperties(Map formModelJson) {
    //初始化模版分支条件
    Map<String, String> formBranchConditionMap = Maps.newHashMap();
    //初始化模版属性
    Map formPropertiesMap = Maps.newHashMap();
    //获取模版部门是否为分支条件
    String isConditionDepartment = String.valueOf(formModelJson.get("isConditionDepartment"));
    if (isConditionDepartment.equalsIgnoreCase("true")) {
      String deptLevel = String.valueOf(formModelJson.get("conditionDepartment"));
      if (StringUtils.isBlank(deptLevel)) {
        throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_DEPARTMENT_LEVEL_WRONG);
      }
      String deptConditionKey = "dept";
      String deptConditionValue = "department_" + deptLevel;
      formBranchConditionMap.put(deptConditionKey, deptConditionValue);
    }
    //获取模版内容的分组信息
    List formModelGroups = (List) formModelJson.get("groups");
    if (formModelGroups.size() <= 0) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_GROUPS);
    }
    formModelGroups.forEach(group -> {
      Map groupMap = CommonUtil.o2m(group);
      List children = (List) groupMap.get("children");
      if (children.size() > 0) {
        children.forEach(child -> {
          Map childMap = CommonUtil.o2m(child);
          //若为分支条件，则获取分支条件
          if (String.valueOf(childMap.get("isCondition")).equalsIgnoreCase("true")) {
            //获取当前字段的流程节点分支条件
            Map<String, String> conditionMap = getColumnCondition(childMap);
            //将当前节点分支条件放入表单分支条件map
            conditionMap.forEach(formBranchConditionMap::put);
          }
        });
      }
    });
    //将模版分支条件放入表单属性Map
    formPropertiesMap.put("formBranchConditions", new Gson().toJson(formBranchConditionMap));

    return formPropertiesMap;
  }

  /**
   * 获取表单一个字段的的条件分支
   *
   * @param childMap
   * @return
   */
  private Map<String, String> getColumnCondition(Map childMap) {
    Map<String, String> columnConditionMap = Maps.newHashMap();
    //模版字段名称
    String title = String.valueOf(childMap.get("title"));
    //复选框作为流程分支条件
    if (String.valueOf(childMap.get("type")).equalsIgnoreCase("checkbox")) {
      //获取选项列表
      List optionList = (List) childMap.get("options");
      //条件字段复选框内容不能为空
      if (optionList.size() <= 0) {
        throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_CHECKBOX_OPTIONS, "问题字段：" + title);
      }
      //选项集合
      Set<String> optionSet = Sets.newHashSet();
      optionList.forEach(option -> optionSet.add(String.valueOf(option)));
      //表单设计者设置并填写了关联code，从表单获取
      if (String.valueOf(childMap.get("isConditionEdit")).equalsIgnoreCase("true")) {
        List conditionLinkCodeList = (List) childMap.get("conditionLinkCode");
        //流程分支条件复选框关联条数需和选项option数目相同
        if (conditionLinkCodeList.size() != optionList.size()) {
          throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_CHECKBOX_OPTIONS_CONDITION_LINK, "问题字段：" + title);
        }
        conditionLinkCodeList.forEach(linkCode -> {
          String[] columnConditionKV = String.valueOf(linkCode).split("---");
          if (columnConditionKV.length != 2) {
            throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_TABLE_LINK_CODE_WRONG, "问题字段：" + title);
          }
          if (!optionSet.contains(columnConditionKV[1].trim())) {
            throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_CHECKBOX_OPTIONS_CONDITION_LINK_EQUAL, "问题字段：" + title);
          }
          String columnConditionValue = "v_" + title + "_" + columnConditionKV[1].trim();
          columnConditionMap.put(columnConditionKV[0].trim(), columnConditionValue);
        });
      } else { //未填写关联code，自动生成
        optionList.forEach(option -> {
          String columnConditionValue = "v_" + title + "_" + String.valueOf(option);
          columnConditionMap.put(String.valueOf(option), columnConditionValue);
        });
      }
    }//表格字段作为流程分支条件，需要表单设计者自己填写，后台获取
    else if (String.valueOf(childMap.get("type")).equalsIgnoreCase("table")) {
      //获取设计者填写的条件列表
      List conditionLinkCodeList = (List) childMap.get("conditionLinkCode");
      if (conditionLinkCodeList.size() <= 0) {
        throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_TABLE_LINK_CODE, "问题字段：" + title);
      }
      conditionLinkCodeList.forEach(linkCode -> {
        String[] columnConditionKV = String.valueOf(linkCode).split("---");
        if (columnConditionKV.length != 2) {
          throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_TABLE_LINK_CODE_WRONG, "问题字段：" + title);
        }
        columnConditionMap.put(columnConditionKV[0].trim(), columnConditionKV[1].trim());
      });
    }//其他类型作为流程分支条件
    else {
      //表单设计者设置并填写了关联code，从表单获取
      if (String.valueOf(childMap.get("isConditionEdit")).equalsIgnoreCase("true")) {
        List conditionLinkCodeList = (List) childMap.get("conditionLinkCode");
        if (conditionLinkCodeList.size() <= 0) {
          throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_TABLE_LINK_CODE, "问题字段：" + title);
        }
        String conditionLinkCodeOption = String.valueOf(conditionLinkCodeList.get(0));
        String[] columnConditionKV = conditionLinkCodeOption.split("---");
        if (columnConditionKV.length != 2) {
          throw new FlowModelException(ResultEnum.FLOW_MODEL_MODEL_JSON_CONDITION_TABLE_LINK_CODE_WRONG, "问题字段：" + title);
        }
        String columnConditionValue = "k_" + title;
        columnConditionMap.put(columnConditionKV[0].trim(), columnConditionValue);
      } else { //未填写关联code，自动生成
        String columnConditionValue = "k_" + title;
        columnConditionMap.put(title, columnConditionValue);
      }
    }

    return columnConditionMap;
  }

  /**
   * 更新表单
   */
  @Override
  public FlowModelDto updateFlowMode(FlowModelDto flowModelDto) throws Exception {

    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    //判断前端所传数据中关键字段是否为空或空串或由空白符(whitespace)构成
    if (StringUtils.isBlank(flowModelDto.getFormKey())) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_KEY_NULL);
    }
    if (StringUtils.isBlank(flowModelDto.getFormName())) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_NAME_NULL);
    }
    if (StringUtils.isBlank(flowModelDto.getFormDept())) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_DEPT_NULL);
    }

    //判断待更新表单是否在数据库中
    if (getFlowModelDtoByFormKey(flowModelDto.getFormKey()) == null) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_KEY_ERROR);
    } else if (getFlowModelByFormName(flowModelDto.getFormName()) == null) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_NAME_ERROR);
    }
    FlowModel flowModel = flowModelDtoToModel(flowModelDto);
    //获得版本最高的旧版本
    FlowModel flowModelOld = flowModelRepository
        .findFirstByFormKeyOrderByFormVersionDesc(flowModelDto.getFormKey());
    //若仅更新模版，则设置分支条件,节点审批人及节点可编辑字段同旧模版相同
    if (flowModelDto.getUpdateType().equalsIgnoreCase("onlyForm")) {
      flowModel.setFormBranchConditions(flowModelOld.getFormBranchConditions());
      flowModel.setNodeApproveUsers(flowModelOld.getNodeApproveUsers());
      flowModel.setEditNode(flowModelOld.getEditNode());
      flowModel.setNodeProperty(flowModelOld.getNodeProperty());
      //更新模版全部属性
    } else if (flowModelDto.getUpdateType().equalsIgnoreCase("all")) {
      //获取表单模版相关的属性，包括节点分支条件、可编辑节点
      Map formProperties = getFormProperties(flowModelDto.getFormModelJson());
      //设置模版节点分支条件
      flowModel.setFormBranchConditions(String.valueOf(formProperties.get("formBranchConditions")));
      flowModel.setNodeApproveUsers(flowModelOld.getNodeApproveUsers());
      flowModel.setEditNode(flowModelOld.getEditNode());
      flowModel.setNodeProperty(flowModelOld.getNodeProperty());
    } else { //做兼容，默认仅更新模版
      flowModel.setFormBranchConditions(flowModelOld.getFormBranchConditions());
      flowModel.setNodeApproveUsers(flowModelOld.getNodeApproveUsers());
      flowModel.setEditNode(flowModelOld.getEditNode());
      flowModel.setNodeProperty(flowModelOld.getNodeProperty());
    }
    //老版本未启用，则新版本未启用;老版本启用，则新版本置为启用，老版本置为未启用
    if (flowModelOld.getFlowStatus().equals(Constant.FLOW_UN_START_USED)) {
      flowModel.setFlowStatus(Constant.FLOW_UN_START_USED);
      flowModel.setFormVersion(flowModelOld.getFormVersion() + 1);
      return flowModelToDto(flowModelRepository.save(flowModel));
    } else if (flowModelOld.getFlowStatus().equals(Constant.FLOW_START_USED)) {
      flowModelOld.setFlowStatus(Constant.FLOW_UN_START_USED);
      flowModel.setFlowStatus(Constant.FLOW_START_USED);
      flowModel.setFormVersion(flowModelOld.getFormVersion() + 1);
      flowModelRepository.save(flowModelOld);
      return flowModelToDto(flowModelRepository.save(flowModel));
    } else { //草稿更新,更新后仍是草稿且覆盖原草稿
      flowModel.setFormVersion(flowModelOld.getFormVersion());
      flowModel.setId(flowModelOld.getId());
      flowModel.setFlowStatus(Constant.FLOW_DRAFT_FLOW);
      return flowModelToDto(flowModelRepository.save(flowModel));
    }
  }

  /**
   * 获取表单所有部门
   */
  @Override
  public List<String> getFlowModelAllDept() {
    return flowModelRepository.findAllDept();
  }

  /**
   * 根据条件获取流程模版
   *
   * @param dept 查询条件类
   * @return 表单formName，formKey
   */
  @Override
  public Object getFlowModels(DeptDto dept) {
    if ("全部部门".equals(dept.getDept()) || "all_department".equals(dept.getDept())) {
      dept.setDept("");
    }
    List<FlowModelNameDto> flowModelNames = Lists.newArrayList();
    Map<String, String> flowNames = Maps.newHashMap();
    Optional.of(flowModelRepository.findAll((Specification<FlowModel>) (root, query, cb) -> {
      Predicate predicate = cb.conjunction();
      if (StringUtils.isNotBlank(dept.getDept())) {
        predicate.getExpressions().add(cb.equal(root.get("formDept"), dept.getDept()));
      }
      if (StringUtils.isNotBlank(dept.getFormKey())) {
        predicate.getExpressions().add(cb.like(root.get("formKey"), "%" + dept.getFormKey() + "%"));
      }
      if (StringUtils.isNotBlank(dept.getFormName())) {
        predicate.getExpressions()
            .add(cb.equal(root.get("formName"), "%" + dept.getFormName() + "%"));
      }
      if (dept.getFlowStatus() == null) {
        predicate.getExpressions().add(cb.equal(root.get("flowStatus"), Constant.FLOW_START_USED));
      } else {
        predicate.getExpressions().add(cb.equal(root.get("flowStatus"), dept.getFlowStatus()));
      }
      return predicate;
    })).ifPresent(flowModels -> flowModels.forEach(flowModel -> {
      addFlowModel(flowModelNames, flowNames, flowModel);
    }));
    return flowModelNames;
  }

  /**
   * 根据部门名称获取流程模版
   *
   * @param dept 部门名称
   * @return 表单列表
   */
  @Override
  public List<FlowModelNameDto> getFlowModelsByDept(String dept) {
    List<FlowModelNameDto> flowModelNames = Lists.newArrayList();
    Map<String, String> flowNames = Maps.newHashMap();
    if (dept == null || "".equals(dept)) {
      Optional.of(flowModelRepository.findAll())
          .ifPresent(flowModels -> flowModels.forEach(flowModel -> {
            addFlowModel(flowModelNames, flowNames, flowModel);
          }));
    } else {
      Optional.of(flowModelRepository.findByFormDept(dept))
          .ifPresent(flowModels -> flowModels.forEach(flowModel -> {
            addFlowModel(flowModelNames, flowNames, flowModel);
          }));
    }
    return flowModelNames;
  }

  /**
   * 分页获取所有流程模版
   */
  @Override
  public Page<FlowModel> getFlowModelsPageAble(DeptDto dept) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    return flowModelRepository.findAll((Specification<FlowModel>) (root, query, cb) -> {
      Predicate predicate = cb.conjunction();
      if (StringUtils.isNotBlank(dept.getDept())) {
        predicate.getExpressions().add(cb.equal(root.get("formDept"), dept.getDept().trim()));
      }
      if (StringUtils.isNotBlank(dept.getFormKey())) {
        predicate.getExpressions().add(cb.like(root.get("formKey"), "%" + dept.getFormKey().trim() + "%"));
      }
      if (StringUtils.isNotBlank(dept.getFormName())) {
        predicate.getExpressions()
            .add(cb.or(cb.like(root.get("formName"), "%" + dept.getFormName().trim() + "%"),
                cb.like(root.get("formKey"), "%" + dept.getFormName().trim() + "%")));
      }
      if (dept.getFlowStatus() != null) {
        predicate.getExpressions().add(cb.equal(root.get("flowStatus"), dept.getFlowStatus()));
      }
      return predicate;
    }, dept.getPageAble());
  }

  /**
   * 查询热门流程模版
   */
  @Override
  public List<FlowModelNameDto> getMyFlowModels() {
    Map<String, String> flowNames = Maps.newHashMap();
    List<FlowModelNameDto> resule = new ArrayList<>();
    List<FlowModelNameDto> flowModelNames = flowOrderRepository.findByQtalk(SecurityUtils.currentUsername());
    flowModelNames.forEach(flowModelNameDto -> {
      List<FlowModel> flowModels = flowModelRepository.findAllByFlowStatusAndFormKey(Constant.FLOW_START_USED, flowModelNameDto.getFormKey());
      if (flowModels != null && flowModels.size() > 0 && resule.size() < 10) {
        flowModelNameDto.setFormMark("qoa");
        resule.add(flowModelNameDto);
        flowNames.put(flowModelNameDto.getFormKey(), flowModelNameDto.getFormName());
      }
    });
    if (resule.size() < 10) {
      Optional.of(flowModelRepository.findAllByFlowStatus(Constant.FLOW_START_USED))
          .ifPresent(flowModels -> flowModels.forEach(flowModel -> {
            if (resule.size() < 10) {
              addFlowModel(resule, flowNames, flowModel);
            }
          }));
    }
    return resule;
  }

  /**
   * 更新表单状态
   *
   * @param flowStatusDto 表单id
   * @return 操作成功的表单
   */
  @Override
  public FlowModelDto updateFlowModelStatus(FlowStatusDto flowStatusDto) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    Optional<FlowModel> flowModel = flowModelRepository.findById(flowStatusDto.getId());
    //判空
    if (!flowModel.isPresent()) {
      throw new FlowModelException(ResultEnum.FLOW_MODEL_NOT_EXISTS);
    }
    //草稿变未启用，未启用变启用，启用变未启用
    if (flowModel.get().getFlowStatus().equals(Constant.FLOW_DRAFT_FLOW)) {
      flowModel.get().setFlowStatus(Constant.FLOW_UN_START_USED);
    } else if (flowModel.get().getFlowStatus().equals(Constant.FLOW_UN_START_USED)) {
      flowModel.get().setFlowStatus(Constant.FLOW_START_USED);
    } else {
      flowModel.get().setFlowStatus(Constant.FLOW_UN_START_USED);
    }
    return flowModelToDto(flowModelRepository.save(flowModel.get()));
  }

  @Override
  public List<String> getAllDeptByCompany() {
    return departmentRepository.findAllDeptNameByDeep(1);
  }

  @Override
  public FlowModelDto getFlowModelDtoByFormKey(String formKey) {
    FlowModel flowModel = flowModelRepository.findFirstByFormKeyOrderByFormVersionDesc(formKey);
    if (flowModel == null) {
      return null;
    }
    return new FlowModelDto(flowModel);
  }

  @Override
  public FlowModel getFlowModelByFormKey(String formKey) {
    return flowModelRepository.findFirstByFormKeyAndFlowStatusOrderByFormVersionDesc(formKey, 1);
  }

  @Override
  public FlowModelDto getFlowModelDtoByFormKeyAndVersion(String formKey, String version) {
    FlowModel flowModel = getFlowModelByFormKeyAndVersion(formKey, version);
    if (flowModel == null) {
      return null;
    }
    List<Map<String, Object>> editNodeName = new ArrayList<>();
    if (StringUtils.isNotBlank(flowModel.getEditNode())) {
      Map<String, Object> map = CommonUtil.s2m(flowModel.getEditNode());
      for (String key : map.keySet()) {
        editNodeName.addAll((List) map.get(key));
      }
    }
    return new FlowModelDto(flowModel, editNodeName);
  }

  @Override
  public FlowModel getFlowModelByFormKeyAndVersion(String formKey, String version) {
    FlowModel flowModel;
    if (StringUtils.isNotBlank(version)) {
      flowModel = flowModelRepository.findByFormKeyAndFormVersion(formKey, Integer.parseInt(version));
    } else {
      flowModel = flowModelRepository.findFirstByFormKeyAndFlowStatusOrderByFormVersionDesc(formKey, 1);
    }
    if (flowModel == null) {
      return null;
    }
    return flowModel;
  }

  @Override
  public FlowModelDto getFlowModelByFormName(String formName) {
    FlowModel flowModel = flowModelRepository.findFirstByFormNameOrderByFormVersionDesc(formName);
    if (flowModel == null) {
      return null;
    }
    return new FlowModelDto(flowModel);
  }

}
