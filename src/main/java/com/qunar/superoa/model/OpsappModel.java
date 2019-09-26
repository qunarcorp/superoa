package com.qunar.superoa.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @Auther: lee.guo
 * @Despriction: opsapp最外层封装
 * @Date: Created in 6:14 PM 2018/10/16
 * @Modify by:
 */
@Data
@ApiModel("opsapp参数数据")
public class OpsappModel {

  @ApiModelProperty("dataSource")
  private List dataSource;

  public List<Map<String, Object>> getDataSource() {
    return dataSource;
  }

  public OpsappModel(List<?> data, Boolean isPatch) {
    List<Map<String, Object>> dataSource = new ArrayList();
    Map<String, Object> dataSourceM = new HashMap<>();
    dataSourceM.put("processKeys", "QUNAR_SUPER_OA");
    if (isPatch) {
      dataSourceM.put("add", new ArrayList<>());
      dataSourceM.put("remove", data);
    } else {
      dataSourceM.put("data", data);
    }
    dataSource.add(dataSourceM);
    this.dataSource = dataSource;
  }
}
