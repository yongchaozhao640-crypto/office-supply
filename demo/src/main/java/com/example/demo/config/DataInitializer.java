package com.example.demo.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private DataSource dataSource;
    @Autowired private OfficeSupplyMapper supplyMapper;
    @Autowired private DepartmentMapper deptMapper;
    @Autowired private PersonnelMapper personMapper;
    @Autowired private PurchaseRecordMapper purchaseMapper;
    @Autowired private UsageRecordMapper usageMapper;
    @Autowired private InventoryMapper inventoryMapper;
    @Autowired private UserMapper userMapper;

    // cache supply name -> id mapping for fast lookup during import
    private Map<String, Integer> supplyNameToId;

    @PostConstruct
    public void init() {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/schema.sql"));
            populator.setSeparator(";");
            populator.setSqlScriptEncoding(StandardCharsets.UTF_8.name());
            populator.setContinueOnError(true);
            populator.execute(dataSource);

            createDefaultAdmin();

            if (supplyMapper.selectCount(new QueryWrapper<>()) > 0) {
                log.info("Database already initialized, skipping data import");
                return;
            }

            importFromExcel();
            log.info("Database initialized successfully from Excel");
        } catch (Exception e) {
            log.error("Failed to initialize database: {}", e.getMessage(), e);
        }
    }

    private void createDefaultAdmin() {
        User admin = userMapper.selectOne(new QueryWrapper<User>().eq("username", "admin"));
        if (admin == null) {
            admin = new User();
            admin.setName("管理员");
            admin.setUsername("admin");
            admin.setPassword(new BCryptPasswordEncoder().encode("123456"));
            admin.setRole("admin");
            userMapper.insert(admin);
            log.info("Default admin user created: admin / 123456");
        }
    }

    private void importFromExcel() throws Exception {
        InputStream is = new ClassPathResource("data.xlsx").getInputStream();
        Workbook wb = new XSSFWorkbook(is);

        importOfficeSupply(wb.getSheetAt(1));
        refreshSupplyNameCache();
        importDeptAndPersonnel(wb.getSheetAt(2));
        importPurchaseRecord(wb.getSheetAt(3));
        importUsageRecord(wb.getSheetAt(4));
        importSafetyStock(wb.getSheetAt(9));

        wb.close();
        is.close();
    }

    private void refreshSupplyNameCache() {
        supplyNameToId = new HashMap<>();
        List<OfficeSupply> list = supplyMapper.selectList(new QueryWrapper<>());
        for (OfficeSupply s : list) {
            supplyNameToId.put(s.getName(), s.getId());
        }
    }

    private void importOfficeSupply(Sheet sheet) {
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String noStr = getCellStr(row, 1);
            if (noStr.isEmpty()) continue;
            try { Integer.parseInt(noStr); } catch (NumberFormatException e) { continue; }
            String name = getCellStr(row, 2);
            if (name.isEmpty()) continue;
            OfficeSupply s = new OfficeSupply();
            s.setSupplyNo(Integer.parseInt(noStr));
            s.setName(name);
            s.setUnit(getCellStr(row, 3));
            s.setModel(getCellStr(row, 4));
            s.setUnitPrice(getCellDecimal(row, 5));
            s.setWarningStock(new BigDecimal("5"));
            s.setRemark(getCellStr(row, 6));
            supplyMapper.insert(s);
        }
        log.info("Imported {} office supplies", supplyMapper.selectCount(new QueryWrapper<>()));
    }

    private void importDeptAndPersonnel(Sheet sheet) {
        Set<String> deptSet = new LinkedHashSet<>();
        List<Row> personRows = new ArrayList<>();

        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String noStr = getCellStr(row, 1);
            if (noStr.isEmpty()) continue;
            try { Integer.parseInt(noStr); } catch (NumberFormatException e) { continue; }
            String deptName = getCellStr(row, 3);
            if (!deptName.isEmpty()) deptSet.add(deptName);
            personRows.add(row);
        }

        for (int i = 4; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String deptName = getCellStr(row, 7);
            if (!deptName.isEmpty()) deptSet.add(deptName);
        }

        int deptNo = 1;
        for (String name : deptSet) {
            Department d = new Department();
            d.setDeptNo(deptNo++);
            d.setName(name);
            deptMapper.insert(d);
        }
        log.info("Imported {} departments", deptSet.size());

        for (Row row : personRows) {
            Personnel p = new Personnel();
            p.setPersonNo(Integer.parseInt(getCellStr(row, 1)));
            p.setName(getCellStr(row, 2));
            p.setDeptName(getCellStr(row, 3));
            personMapper.insert(p);
        }
        log.info("Imported {} personnel", personRows.size());
    }

    private void importPurchaseRecord(Sheet sheet) {
        int count = 0;
        int noIdCount = 0;
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String noStr = getCellStr(row, 1);
            if (noStr.isEmpty()) continue;
            try { Integer.parseInt(noStr); } catch (NumberFormatException e) { continue; }
            String supplyName = getCellStr(row, 4);
            Integer supplyId = supplyNameToId.get(supplyName);
            if (supplyId == null) {
                if (noIdCount < 5) log.warn("Purchase row {}: supply '{}' not found in office_supply", i, supplyName);
                noIdCount++;
            }
            BigDecimal qty = getCellDecimal(row, 8);
            BigDecimal price = getCellDecimal(row, 7);
            PurchaseRecord r = new PurchaseRecord();
            r.setPurchaseNo(Integer.parseInt(noStr));
            r.setPurchaseDate(getCellDate(row, 2));
            r.setPurchaser(getCellStr(row, 3));
            r.setSupplyId(supplyId);
            r.setSupplyName(supplyName);
            r.setUnit(getCellStr(row, 5));
            r.setUnitPrice(price);
            r.setQuantity(qty);
            r.setAmount(qty != null && price != null ? qty.multiply(price) : getCellDecimal(row, 9));
            r.setRemark(getCellStr(row, 10));
            r.setYear(getCellInt(row, 11));
            r.setMonth(getCellInt(row, 12));
            purchaseMapper.insert(r);
            count++;
        }
        log.info("Imported {} purchase records ({} without supply_id match)", count, noIdCount);
    }

    private void importUsageRecord(Sheet sheet) {
        int count = 0;
        int noIdCount = 0;
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String noStr = getCellStr(row, 1);
            if (noStr.isEmpty()) continue;
            try { Integer.parseInt(noStr); } catch (NumberFormatException e) { continue; }
            String supplyName = getCellStr(row, 5);
            Integer supplyId = supplyNameToId.get(supplyName);
            if (supplyId == null) {
                if (noIdCount < 5) log.warn("Usage row {}: supply '{}' not found in office_supply", i, supplyName);
                noIdCount++;
            }
            BigDecimal qty = getCellDecimal(row, 9);
            BigDecimal price = getCellDecimal(row, 8);
            UsageRecord r = new UsageRecord();
            r.setUsageNo(Integer.parseInt(noStr));
            r.setUsageDate(getCellDate(row, 2));
            r.setPersonName(getCellStr(row, 3));
            r.setDeptName(getCellStr(row, 4));
            r.setSupplyId(supplyId);
            r.setSupplyName(supplyName);
            r.setUnit(getCellStr(row, 6));
            r.setUnitPrice(price);
            r.setQuantity(qty);
            r.setAmount(qty != null && price != null ? qty.multiply(price) : getCellDecimal(row, 10));
            r.setRemark(getCellStr(row, 11));
            r.setYear(getCellInt(row, 12));
            r.setMonth(getCellInt(row, 13));
            usageMapper.insert(r);
            count++;
        }
        log.info("Imported {} usage records ({} without supply_id match)", count, noIdCount);
    }

    private void importSafetyStock(Sheet sheet) {
        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String noStr = getCellStr(row, 1);
            if (noStr.isEmpty()) continue;
            try { Integer.parseInt(noStr); } catch (NumberFormatException e) { continue; }
            int supplyNo = Integer.parseInt(noStr);
            BigDecimal safety = getCellDecimal(row, 8);
            String needPurchase = getCellStr(row, 9);

            if (safety != null || (needPurchase != null && !needPurchase.isEmpty())) {
                Inventory inv = inventoryMapper.selectOne(new QueryWrapper<Inventory>().eq("supply_no", supplyNo));
                if (inv == null) {
                    inv = new Inventory();
                    inv.setSupplyNo(supplyNo);
                    inv.setSupplyName(getCellStr(row, 2));
                    inv.setUnit(getCellStr(row, 3));
                    inv.setOpeningQty(BigDecimal.ZERO);
                    inv.setOpeningAmount(BigDecimal.ZERO);
                    inv.setCurrentQty(BigDecimal.ZERO);
                    inv.setCurrentAmount(BigDecimal.ZERO);
                    inv.setSafetyStock(safety != null ? safety : BigDecimal.ZERO);
                    inv.setNeedPurchase(needPurchase != null ? needPurchase : "");
                    inv.setVersion(0);
                    inventoryMapper.insert(inv);
                } else {
                    if (safety != null) inv.setSafetyStock(safety);
                    if (needPurchase != null && !needPurchase.isEmpty()) inv.setNeedPurchase(needPurchase);
                    inventoryMapper.updateById(inv);
                }
            }
        }
        log.info("Imported safety stock settings from sheet 9");
    }

    // --- Cell helpers ---
    private String getCellStr(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                double v = cell.getNumericCellValue();
                if (v == Math.floor(v) && !Double.isInfinite(v)) return String.valueOf((long) v);
                return String.valueOf(v);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue().trim(); }
                catch (Exception e) { return String.valueOf(cell.getNumericCellValue()); }
            default: return "";
        }
    }

    private BigDecimal getCellDecimal(Row row, int col) {
        String s = getCellStr(row, col);
        if (s.isEmpty() || s.equals("-")) return null;
        try { return new BigDecimal(s); } catch (NumberFormatException e) { return null; }
    }

    private Integer getCellInt(Row row, int col) {
        String s = getCellStr(row, col);
        if (s.isEmpty()) return null;
        try { return (int) Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }

    private Date getCellDate(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            }
            String s = getCellStr(row, col);
            if (s.isEmpty()) return null;
            double d = Double.parseDouble(s);
            return DateUtil.getJavaDate(d);
        } catch (Exception e) {
            return null;
        }
    }
}
