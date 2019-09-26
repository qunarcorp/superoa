package com.qunar.superoa.dto;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @Auther: xing.zhou
 * @Despriction:
 * @Date:Created in 19:44 2018/11/2
 * @Modify by:
 */
public class PageableDto implements Pageable {

  private int pageSize;
  private int pageNumber;

  public PageableDto() {
  }

  public PageableDto(int page, int size) {
    this.pageNumber = page;
    this.pageSize = size;
  }

  @Override
  public int getPageNumber() {
    return this.pageNumber;
  }

  @Override
  public int getPageSize() {
    return this.pageSize;
  }

  @Override
  public long getOffset() {
    return 0;
  }

  @Override
  public Sort getSort() {
    return null;
  }

  @Override
  public Pageable next() {
    return null;
  }

  @Override
  public Pageable previousOrFirst() {
    return null;
  }

  @Override
  public Pageable first() {
    return null;
  }

  @Override
  public boolean hasPrevious() {
    return false;
  }
}
