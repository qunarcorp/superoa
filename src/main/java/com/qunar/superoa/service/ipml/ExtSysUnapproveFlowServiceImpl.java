package com.qunar.superoa.service.ipml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.superoa.constants.Constant;
import com.qunar.superoa.dao.ExtSysUnapproveFlowRepository;
import com.qunar.superoa.dto.ExtSysResultDto;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.QueryExtSysUnapproveDto;
import com.qunar.superoa.model.ExtSysUnapproveFlow;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.ExtSysUnapproveFlowServiceI;
import com.qunar.superoa.utils.CommonUtil;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * @Auther: chengyan.liang
 * @Despriction: 集成系统待审批流程service实现层
 * @Date:Created in 4:44 PM 2019/4/26
 * @Modify by:
 */
@Slf4j
@Component
public class ExtSysUnapproveFlowServiceImpl implements ExtSysUnapproveFlowServiceI {

  @Autowired
  private ExtSysUnapproveFlowRepository extSysUnapproveFlowRepository;

  @Override
  public List<ExtSysResultDto> updateExtSysUnapproveFlow(Object dataSource) {
    List<ExtSysResultDto> resultDtoList;
    //审批系统唯一标识
    String processKeys = String.valueOf(CommonUtil.o2m(dataSource).get("processKeys"));
    //待更新流程审批列表
    List updateUnapproveList = (List) CommonUtil.o2m(dataSource).get("data");
    resultDtoList = updateExtSysUnapproveList(processKeys, updateUnapproveList);
    return resultDtoList;
  }

  @Override
  public List<ExtSysResultDto> deleteExtSysUnapproveFlow(Object dataSource) {
    //结果列表
    List<ExtSysResultDto> resultDtoList;
    //审批系统唯一标识
    String processKeys = String.valueOf(CommonUtil.o2m(dataSource).get("processKeys"));
    //待删除流程审批列表
    List delUnapproveList = (List) CommonUtil.o2m(dataSource).get("data");
    //删除待审批流程
    resultDtoList = delExtSysUnapproveList(processKeys, delUnapproveList);
    return resultDtoList;
  }


  @Override
  public  Map<String, List<ExtSysResultDto>> updatePatchExtSysUnapproveFlow(Object dataSource) {
    //结果列表
    Map<String, List<ExtSysResultDto>> resultMap = Maps.newHashMap();
    List<ExtSysResultDto> delResultDtoList;
    List<ExtSysResultDto> addResultDtoList;
    //审批系统唯一标识
    String processKeys = String.valueOf(CommonUtil.o2m(dataSource).get("processKeys"));
    //待添加流程审批列表
    List addUnapproveList = (List) CommonUtil.o2m(dataSource).get("add");
    //待删除流程审批列表
    List delUnapproveList = (List) CommonUtil.o2m(dataSource).get("remove");
    //删除审批列表
    delResultDtoList = delExtSysUnapproveList(processKeys, delUnapproveList);
    //更新审批列表
    addResultDtoList = updateExtSysUnapproveList(processKeys, addUnapproveList);
    resultMap.put("add", addResultDtoList);
    resultMap.put("remove", delResultDtoList);
    return resultMap;
  }

  /**
   * 更新待审批流程列表
   * @param processKeys 集成系统唯一标识
   * @param updateUnapproveList 待删除审批流程列表
   * @return 结果集
   */
  private List<ExtSysResultDto> updateExtSysUnapproveList(String processKeys, List updateUnapproveList) {
    List<ExtSysResultDto> resultDtoList = Lists.newArrayList();
    if (updateUnapproveList.size() <= 0) {
      return getNullResult(resultDtoList, "add");
    }
    updateUnapproveList.forEach(dataItem -> {
      try {
        ExtSysUnapproveFlow extSysUnapproveFlow = getExtSysUnapproveFlow(processKeys, dataItem);
        ExtSysResultDto extSysResultDto = new ExtSysResultDto();
        //检验跳转链接或待审批人非空
        if (StringUtils.isBlank(extSysUnapproveFlow.getRedirectUrl()) || StringUtils.isBlank(extSysUnapproveFlow.getApproveUsers())) {
          extSysResultDto.setOk(false);
          extSysResultDto.setOperation("add");
          extSysResultDto.setErrorFlow(String.valueOf(CommonUtil.o2m(dataItem).get("oid")));
          extSysResultDto.setMsg("跳转链接或审批人为空");
        } else {
          extSysUnapproveFlowRepository.save(extSysUnapproveFlow);
          extSysResultDto.setOk(true);
          extSysResultDto.setOperation("add");
          extSysResultDto.setMsg(String.valueOf(CommonUtil.o2m(dataItem).get("oid")));
        }
        resultDtoList.add(extSysResultDto);
      } catch (Exception e) {
        ExtSysResultDto extSysResultDto = getUnapproveExceptionResult(dataItem, e, "add");
        resultDtoList.add(extSysResultDto);
      }
    });
    return resultDtoList;
  }

  /**
   * 删除待审批流程列表
   * @param processKeys 集成系统唯一标识
   * @param delUnapproveList 待删除审批流程列表
   * @return 结果集
   */
  private List<ExtSysResultDto> delExtSysUnapproveList(String processKeys, List delUnapproveList) {
    List<ExtSysResultDto> resultDtoList = Lists.newArrayList();
    if (delUnapproveList.size() <= 0) {
      return getNullResult(resultDtoList, "remove");
    }
    delUnapproveList.forEach(dataItem -> {
      try {
        Map<String, Object> unApproveFlowMap = CommonUtil.o2m(dataItem);
        String oid = String.valueOf(unApproveFlowMap.get("oid"));
        List<ExtSysUnapproveFlow> unapproveFlowList = extSysUnapproveFlowRepository.findByOidAndAndProcessKeysOrderByUpdateTimeDesc(oid, processKeys);
        if (unapproveFlowList.size() > 0) {
          extSysUnapproveFlowRepository.deleteAll(unapproveFlowList);
        }
        ExtSysResultDto extSysResultDto = new ExtSysResultDto();
        extSysResultDto.setOk(true);
        extSysResultDto.setOperation("remove");
        extSysResultDto.setMsg(oid);
        resultDtoList.add(extSysResultDto);
      } catch (Exception e) {
        ExtSysResultDto extSysResultDto = getUnapproveExceptionResult(dataItem, e, "remove");
        resultDtoList.add(extSysResultDto);
      }
    });
    return resultDtoList;
  }

  /**
   * 根据参数获取一条待审批流程
   * @param processKeys 集成系统唯一标识
   * @param dataItem 单条参数数据
   * @return 获取一条待审批流程
   */
  private ExtSysUnapproveFlow getExtSysUnapproveFlow(String processKeys, Object dataItem) {
    Map<String, Object> unApproveFlowMap = CommonUtil.o2m(dataItem);
    String oid = String.valueOf(unApproveFlowMap.get("oid"));
    //新建流程审批对象
    ExtSysUnapproveFlow unapproveFlow = new ExtSysUnapproveFlow();
    unapproveFlow.setProcessKeys(processKeys);
    unapproveFlow.setOid(String.valueOf(unApproveFlowMap.get("oid")));
    unapproveFlow.setApplyUser(String.valueOf(unApproveFlowMap.get("applyUser")));
    unapproveFlow.setApproveUsers(String.valueOf(unApproveFlowMap.get("approveUsers")));
    unapproveFlow.setApplyTime(String.valueOf(unApproveFlowMap.get("applyTime")));
    unapproveFlow.setUpdateTime(String.valueOf(unApproveFlowMap.get("updateTime")));
    unapproveFlow.setTitle(String.valueOf(unApproveFlowMap.get("title")));
    unapproveFlow.setRedirectUrl(String.valueOf(unApproveFlowMap.get("redirectUrl")));

    List<ExtSysUnapproveFlow> unapproveFlowList = extSysUnapproveFlowRepository.findByOidAndAndProcessKeysOrderByUpdateTimeDesc(oid, processKeys);
    if (unapproveFlowList.size() > 1) {
      extSysUnapproveFlowRepository.deleteAll(unapproveFlowList);
    } else if (unapproveFlowList.size() == 1) {
      unapproveFlow.setId(unapproveFlowList.get(0).getId());
    }
    return unapproveFlow;
  }

  List<ExtSysResultDto> getNullResult(List<ExtSysResultDto> resultDtoList, String operation) {
    ExtSysResultDto extSysResultDto = new ExtSysResultDto();
    extSysResultDto.setOk(false);
    extSysResultDto.setOperation(operation);
    extSysResultDto.setMsg("流程列表为空");
    resultDtoList.add(extSysResultDto);
    return resultDtoList;
  }

  ExtSysResultDto getUnapproveExceptionResult(Object dataItem, Exception e, String operation) {
    ExtSysResultDto extSysResultDto = new ExtSysResultDto();
    extSysResultDto.setOk(false);
    extSysResultDto.setOperation(operation);
    extSysResultDto.setMsg(e.getMessage());
    extSysResultDto.setErrorFlow(String.valueOf(dataItem));
    return extSysResultDto;
  }

  /**
   * 获取外部系统待我审批的、我发起的进行中流程
   */
  @Override
  public PageResult<ExtSysUnapproveFlow> getMyExtSysUnapproveFlows(QueryExtSysUnapproveDto queryUnapproveDto) {
    String currentUserId = SecurityUtils.currentUsername();
    PageResult<ExtSysUnapproveFlow> pageResult = new PageResult<>();
    Page<ExtSysUnapproveFlow> page = null;
    //我发起的
    if (Constant.FLOW_ORDER_MY_QUERY_TYPE.equals(queryUnapproveDto.getQueryType())) {
      queryUnapproveDto.setUserId(currentUserId);
      page = getMyExtSysStartFlowsByDto(queryUnapproveDto);
    } else if (Constant.FLOW_ORDER_TO_APPROVE_QUERY_TYPE.equals(queryUnapproveDto.getQueryType())) {
      //获取待我审批的
      page = getMyExtSysToApproveFlowsByDto(queryUnapproveDto, currentUserId);
    }
    if (page != null) {
      pageResult.setContent(page.getContent());
      pageResult.setPageable(page.getPageable());
      pageResult.setTotal((int) page.getTotalElements());
    }
    return pageResult;
  }

  /**
   * 获取我发起的外部系统流程实例
   */
  private Page<ExtSysUnapproveFlow> getMyExtSysStartFlowsByDto(QueryExtSysUnapproveDto queryUnapproveDto) {
    return extSysUnapproveFlowRepository.findAll(
        (Specification<ExtSysUnapproveFlow>) (root, query, cb) -> createUnapprovePredicate(root, cb, queryUnapproveDto),
        queryUnapproveDto.getPageAble());
  }

  /**
   * 获取待我审批的外部系统流程实例
   */
  private Page<ExtSysUnapproveFlow> getMyExtSysToApproveFlowsByDto(QueryExtSysUnapproveDto queryUnapproveDto, String currentUserId) {
    return extSysUnapproveFlowRepository.findAll((Specification<ExtSysUnapproveFlow>) (root, query, cb) -> {
      Predicate predicate = createUnapprovePredicate(root, cb, queryUnapproveDto);
      predicate.getExpressions().add(cb.like(root.get("approveUsers"), "%" + currentUserId + "%"));
      return predicate;
    }, queryUnapproveDto.getPageAble());
  }

  /**
   * 创建 查询条件  添加按时间 发起人 流程类型等条件
   */
  private Predicate createUnapprovePredicate(Path root, CriteriaBuilder cb, QueryExtSysUnapproveDto queryUnapproveDto) {
    Predicate predicate = cb.conjunction();
    //提交申请时间查询
    if (StringUtils.isNotBlank(queryUnapproveDto.getSubmitBeginTime())) {
      predicate.getExpressions().add(cb.between(root.get("applyTime"),
          "%" + queryUnapproveDto.getSubmitBeginTime().trim() + "%",
          "%" + queryUnapproveDto.getSubmitEndTime() + "%"));
    }
    //申请人查询
    if (StringUtils.isNotBlank(queryUnapproveDto.getUserId())) {
      predicate.getExpressions().add(cb.like(root.get("applyUser"), "%" + queryUnapproveDto.getUserId() + "%"));
    }
    //关键字查询，包括标题、系统标识匹配
    if (StringUtils.isNotBlank(queryUnapproveDto.getKey())) {
      predicate.getExpressions().add(cb.or(
          cb.like(root.get("title"), "%" + queryUnapproveDto.getKey() + "%"),
          cb.like(root.get("processKeys"), "%" + queryUnapproveDto.getKey() + "%")));
    }
    //系统标识匹配
    if (StringUtils.isNotBlank(queryUnapproveDto.getFlowType())
        && !"全部".equalsIgnoreCase(queryUnapproveDto.getFlowType())) {
      predicate.getExpressions().add(cb.like(root.get("processKeys"), queryUnapproveDto.getFlowType()));
    }
    return predicate;
  }

  /**
   * 获取外部系统我的待办流程数(当前登录人)
   */
  @Override
  public int getExtSysToApproveCount() {
    String userId = SecurityUtils.currentUsername();
    return extSysUnapproveFlowRepository.countByApproveUsersLike(userId);
  }

  /**
   * 获取外部系统当前用户发起的审批中流程数
   */
  @Override
  public int getMyExtSysFlowCount() {
    String userId = SecurityUtils.currentUsername();
    return extSysUnapproveFlowRepository.countByApplyUserLike(userId);
  }

  /**
   * 获取当前用户待审批流程所涉及到的所有外部系统标识
   */
  @Override
  public List<String> getMyExtSysUnapproveProcessKeys() {
    String userId = SecurityUtils.currentUsername();
    return extSysUnapproveFlowRepository.findAllProcessKeys(userId);
  }
}
