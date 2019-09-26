package com.qunar.superoa.service.ipml;

import com.qunar.superoa.dao.GroupRepository;
import com.qunar.superoa.dto.PageResult;
import com.qunar.superoa.dto.QueryWorkGroupDto;
import com.qunar.superoa.enums.ResultEnum;
import com.qunar.superoa.exceptions.GirlException;
import com.qunar.superoa.exceptions.GroupException;
import com.qunar.superoa.model.WorkGroup;
import com.qunar.superoa.security.SecurityUtils;
import com.qunar.superoa.service.GroupServiceI;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * created by chengyan.liang on 2018/9/25
 */
@Service
public class GroupServiceImpl implements GroupServiceI {

  @Autowired
  private GroupRepository groupRepository;

  @Override
  public WorkGroup saveWorkGroup(WorkGroup workGroup) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    WorkGroup workGroup1 = new WorkGroup();
    workGroup1.setName(workGroup.getName().trim());
    workGroup1.setRemarks(workGroup.getRemarks().trim());
    workGroup1.setMembers(workGroup.getMembers().trim());
    if (groupRepository.findByName(workGroup1.getName()).isPresent()) {
      throw new GroupException(ResultEnum.GROUP_IS_HAVE);
    }
    return groupRepository.save(workGroup1);
  }

  @Override
  public WorkGroup findWorkGroupById(Integer id) {
    return groupRepository.findById(id).get();
  }

  @Override
  public void deleteWorkGroupById(Integer id) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return;
    }
    groupRepository.deleteById(id);
  }

  @Override
  public WorkGroup updateWorkGroup(WorkGroup workGroup) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    return groupRepository.save(workGroup);
  }

  @Override
  public PageResult<WorkGroup> findWorkGroupByLike(QueryWorkGroupDto queryWorkGroupDto) {
    if (!SecurityUtils.checkSysAndActivitiAdmin()) {
      return null;
    }
    PageResult<WorkGroup> pageResult = new PageResult<>();
    Page page = groupRepository.findAll((Specification<WorkGroup>) (root, query, cb) -> {
      Predicate predicate = cb.conjunction();
      if (StringUtils.isNotBlank(queryWorkGroupDto.getSearch())) {
        predicate.getExpressions().add(cb.or(cb.like(root.get("name"), "%" + queryWorkGroupDto.getSearch().trim() + "%"),
            cb.like(root.get("members"), "%" + queryWorkGroupDto.getSearch().trim() + "%"),
            cb.like(root.get("remarks"), "%" + queryWorkGroupDto.getSearch().trim() + "%")));
      } else {
        if (StringUtils.isNotBlank(queryWorkGroupDto.getName())) {
          predicate.getExpressions().add(cb.like(root.get("name"), "%" + queryWorkGroupDto.getName() + "%"));
        }
        if (StringUtils.isNotBlank(queryWorkGroupDto.getMembers())) {
          predicate.getExpressions().add(cb.like(root.get("members"), "%" + queryWorkGroupDto.getMembers() + "%"));
        }
      }
      return predicate;
    }, queryWorkGroupDto.getPageAble());
    if (page != null) {
      pageResult.setContent(page.getContent());
      pageResult.setPageable(page.getPageable());
      pageResult.setTotal((int) page.getTotalElements());
    }
    return pageResult;
  }
}
