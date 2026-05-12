package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("department")
public class Department {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer deptNo;
    private String name;
    private String remark;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getDeptNo() { return deptNo; }
    public void setDeptNo(Integer deptNo) { this.deptNo = deptNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
