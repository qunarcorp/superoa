package com.qunar.superoa.utils;

import com.qunar.superoa.constants.ActivitiStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: chengyan.liang
 * @Despriction:
 * @Date:Created in 11:51 AM 2019/3/26
 * @Modify by:
 */
public class ToWeb {

  /**
   * 状态码
   */
  private String status;
  /**
   * 提示信息
   */
  private String msg;
  /**
   * 重定向的url
   */
  private String redirectUrl;
  /**
   * 返回
   */
  private boolean back;
  /**
   * 刷新页面
   */
  private boolean refresh;
  /**
   * 数据信息
   */
  private Map<String, Object> data;

  public ToWeb() {
    data = new HashMap<>();
    /**
     * 额外信息
     */
    data.put("extra", new HashMap<>());
    /**
     * 对象数据
     */
    data.put("obj", null);
    /**
     * 分页列表数据
     */
    data.put("rows", null);
    refresh = false;
    back = false;
    status = ActivitiStatus.SUCCESS;
  }

  public static ToWeb buildResult(){
    return new ToWeb();
  }

  public ToWeb status(String status){
    setStatus(status);
    return this;
  }

  public ToWeb msg(String msg){
    setMsg(msg);
    return this;
  }

  public ToWeb redirectUrl(String redirectUrl){
    setRedirectUrl(redirectUrl);
    return this;
  }

  public ToWeb back(){
    setBack(true);
    return this;
  }

  public ToWeb refresh(){
    setRefresh(true);
    return this;
  }

  public ToWeb putExtra(String name, Object val){
    ((HashMap<String, Object>)data.get("extra")).put(name, val);
    return this;
  }
  public ToWeb setObjData(Object obj){
    data.put("obj", obj);
    return this;
  }

  public ToWeb rows(Rows rows){
    setRows(rows);
    return this;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  public boolean isRefresh() {
    return refresh;
  }

  public void setRefresh(boolean refresh) {
    this.refresh = refresh;
  }

  public boolean isBack() {
    return back;
  }

  public void setBack(boolean back) {
    this.back = back;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  public ToWeb setRows(Rows rows) {
    data.put("rows", rows);
    return this;
  }
  public static class Rows{
    private long totalRows;
    private int current;
    private int rowSize;
    private int totalPages;
    private List list;

    public Rows() {
    }

    public Rows(long totalRows, int current, int rowSize, int totalPages, List list) {
      this.totalRows = totalRows;
      this.current = current;
      this.rowSize = rowSize;
      this.totalPages = totalPages;
      this.list = list;
    }

    public long getTotalRows() {
      return totalRows;
    }

    public Rows setTotalRows(long totalRows) {
      this.totalRows = totalRows;
      return this;
    }

    public int getCurrent() {
      return current;
    }

    public Rows setCurrent(int current) {
      this.current = current;
      return this;
    }

    public int getRowSize() {
      return rowSize;
    }

    public Rows setRowSize(int rowSize) {
      this.rowSize = rowSize;
      return this;
    }

    public int getTotalPages() {
      return totalPages;
    }

    public Rows setTotalPages(int totalPages) {
      this.totalPages = totalPages;
      return this;
    }

    public List getList() {
      return list;
    }

    public Rows setList(List list) {
      this.list = list;
      return this;
    }

    public static Rows buildRows(){
      return new Rows();
    }
  }
}