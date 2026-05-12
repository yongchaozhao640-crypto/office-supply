package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.Personnel;
import com.example.demo.mapper.PersonnelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonnelService {

    @Autowired
    private PersonnelMapper personnelMapper;

    public List<Personnel> list(String name, String deptName) {
        QueryWrapper<Personnel> qw = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            qw.like("name", name);
        }
        if (deptName != null && !deptName.isEmpty()) {
            qw.eq("dept_name", deptName);
        }
        qw.orderByAsc("person_no");
        return personnelMapper.selectList(qw);
    }

    public Personnel getByName(String name) {
        QueryWrapper<Personnel> qw = new QueryWrapper<>();
        qw.eq("name", name);
        return personnelMapper.selectOne(qw);
    }

    public void save(Personnel p) {
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        if (p.getDeptName() == null || p.getDeptName().trim().isEmpty()) {
            throw new RuntimeException("所属部门不能为空");
        }
        if (p.getPersonNo() == null) {
            p.setPersonNo((int)(personnelMapper.selectCount(new QueryWrapper<>()) + 1));
        }
        personnelMapper.insert(p);
    }

    public void update(Personnel p) {
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        if (p.getDeptName() == null || p.getDeptName().trim().isEmpty()) {
            throw new RuntimeException("所属部门不能为空");
        }
        personnelMapper.updateById(p);
    }

    public void delete(Integer id) {
        personnelMapper.deleteById(id);
    }

    public void batchDelete(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            personnelMapper.deleteBatchIds(ids);
        }
    }
}
