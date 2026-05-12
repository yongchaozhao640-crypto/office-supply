package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("personnel")
public class Personnel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer personNo;
    private String name;
    private String deptName;
    private String remark;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPersonNo() { return personNo; }
    public void setPersonNo(Integer personNo) { this.personNo = personNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
