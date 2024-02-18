package com.eunycesoft.psms.views.components;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.views.components.wizard.Wizard;
import com.eunycesoft.psms.views.components.wizard.WizardPage;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.gridpro.GridProVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.OneToMany;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.eunycesoft.psms.Application.repositories;

@Slf4j
public class DataImportWizard<T extends AbstractEntity> extends Wizard {
    private final Class<T> domainType;
    private final String domainTypeName;
    private final MemoryBuffer buffer = new MemoryBuffer();
    private Upload upload = new Upload(buffer);
    private GridPro<FieldToColumnMapping> fieldMapper;
    private Grid<T> dataGrid;
    private Select<XSSFSheet> sheetSelect = new Select<>();

    public DataImportWizard(Class<T> domainType) {
        super(Utils.toHumanFriendly(domainType.getSimpleName()) + " import wizard");
        this.domainType = domainType;
        this.domainTypeName = Utils.toHumanFriendly(domainType.getSimpleName());
        fieldMapper = new GridPro<>(FieldToColumnMapping.class);
        fieldMapper.removeAllColumns();
        dataGrid = new Grid<>(domainType, false);
        Utils.getAllFields(domainType, AbstractEntity.class, false).forEach(field -> {
            if (field.isAnnotationPresent(OneToMany.class)) return;
            fieldMapper.getListDataView().addItem(new FieldToColumnMapping(field, "", null));
            dataGrid.addColumn(field.getName()).setResizable(true).setAutoWidth(true);
        });
        addPage(createFileUploadPage("Upload data spreadsheet"));
        addPage(createFieldsMappingPage("Map entity fields to spreadsheet columns"));
        addPage(createDataViewPage("Uploaded data summary"));
        addPage(createDataSavingPage("Saving data to repository"));
    }

    private WizardPage createFileUploadPage(String title) {
        var page = new WizardPage(title);
        page.setAlignItems(FlexComponent.Alignment.CENTER);
        page.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        upload.setAcceptedFileTypes("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
        upload.addSucceededListener(evt -> {
            try {
                var workbook = new XSSFWorkbook(buffer.getInputStream());
                var sheets = new ArrayList<XSSFSheet>();
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    sheets.add(workbook.getSheetAt(i));
                }
                sheetSelect.setItems(sheets);
                sheetSelect.setValue(sheets.get(0));
                upload.clearFileList();
                this.next();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        page.add(upload);
        return page;
    }

    private WizardPage createFieldsMappingPage(String title) {
        var page = new WizardPage(title);
        page.setAlignItems(FlexComponent.Alignment.CENTER);
        sheetSelect.setItemLabelGenerator(sheet -> sheet.getSheetName());
        page.add(sheetSelect, fieldMapper);
        fieldMapper.addColumn(fm -> {
            if (Utils.isNullable(fm.getField()))
                return fm.getField().getName();
            else return fm.getField().getName() + "*";
        }).setHeader("Field name");
        fieldMapper.addColumn(fm -> fm.getSheetColumnName()).setKey("sheetColumn").setHeader("Sheet column");
        fieldMapper.addThemeVariants(GridProVariant.LUMO_COMPACT, GridProVariant.LUMO_COLUMN_BORDERS);
        fieldMapper.setSizeFull();
        fieldMapper.setEnterNextRow(true);
        fieldMapper.setEditOnClick(true);

        sheetSelect.addValueChangeListener(evt -> {
            if (fieldMapper.getColumnByKey("sheetColumn") != null)
                fieldMapper.removeColumnByKey("sheetColumn");
            fieldMapper.getListDataView().getItems().forEach(fm -> {
                fm.setSheetColumnName("");
                fm.setSheetColumnNumber(null);
            });
            if (evt.getValue() == null || evt.getValue().getRow(0) == null) return;
            var firstRow = evt.getValue().getRow(0);
            int index = 0;
            var sheetColumns = new HashMap<String, Integer>();
            sheetColumns.put("", null);
            for (Cell cell : firstRow) {
                sheetColumns.put(new DataFormatter().formatCellValue(cell), index++);
            }
            var sheetColumnNames = List.copyOf(sheetColumns.keySet());
            fieldMapper.addEditColumn(fm -> fm.getSheetColumnName())
                    .select((fm, str) -> {
                        fm.setSheetColumnName(str);
                        fm.setSheetColumnNumber(sheetColumns.get(str));
                    }, sheetColumnNames)
                    .setKey("sheetColumn").setHeader("Sheet column");
            var distance = new LevenshteinDistance();
            fieldMapper.getListDataView().getItems().forEach(fm -> {
                for (String sheetColName : sheetColumnNames) {
                    if (fm.getField().getName().equalsIgnoreCase(Utils.databaseColumnToCamelCase(sheetColName))
                        || (fm.getField().getName() + "Id").equalsIgnoreCase(Utils.databaseColumnToCamelCase(sheetColName))) {
                        fm.setSheetColumnName(sheetColName);
                        fm.setSheetColumnNumber(sheetColumns.get(sheetColName));
                        break;
                    }
                }
            });
            fieldMapper.getListDataView().refreshAll();
        });
        return page;
    }

    private WizardPage createDataViewPage(String title) {
        var page = new WizardPage(title);
        dataGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS);
        dataGrid.setSizeFull();
        page.add(dataGrid);
        page.addAttachListener(evt -> {
            var data = new ArrayList<T>();
            List<FieldToColumnMapping> mappedFields = fieldMapper.getListDataView().getItems()
                    .filter(fm -> fm.getSheetColumnNumber() != null).collect(Collectors.toList());
            if (mappedFields.isEmpty()) return;
            for (Row row : sheetSelect.getValue()) {
                if (row.getRowNum() == 0) continue;
                data.add(fromRow(row, mappedFields));
            }
            dataGrid.setItems(data);
        });
        return page;
    }

    private WizardPage createDataSavingPage(String title) {
        var page = new WizardPage(title);
        var summary = new Span();
        var log = new VerticalLayout();
        log.setSizeFull();
        log.setSpacing(false);
        log.setPadding(false);
        var saveBtn = new Button("Save");
        page.add(summary, log, saveBtn);
        page.setAlignItems(FlexComponent.Alignment.CENTER);
        AtomicInteger itemsCount = new AtomicInteger();
        page.addAttachListener(evt -> {
            itemsCount.set(dataGrid.getListDataView().getItemCount());
            saveBtn.setEnabled(itemsCount.get() > 0);
            if (itemsCount.get() == 0) summary.setText("No item to save.");
            else summary.setText(String.format("Items: %d, Saved: 0, Failed: 0", itemsCount.get()));
        });
        saveBtn.addClickListener(evt -> {
            var repo = (ExtendedJpaRepository) repositories.getRepositoryFor(domainType).get();
            AtomicInteger saved = new AtomicInteger();
            AtomicInteger count = new AtomicInteger();
            dataGrid.getListDataView().getItems().forEach(item -> {
                count.getAndIncrement();
                var statusLabel = new Span();
                try {
                    repo.save(item);
                    saved.getAndIncrement();
                    statusLabel.setText("Saved");
                    statusLabel.getElement().getThemeList().add("badge success small");
                } catch (Exception ex) {
                    statusLabel.setText("Failed");
                    statusLabel.getElement().getThemeList().add("badge error small");
                    statusLabel.getElement().setAttribute("title", ex.getMessage());
                }
                log.add(new Span(new Text(domainTypeName + " " + count.get() + " "), statusLabel));
                summary.setText(String.format("Items: %d, Saved: %d, Failed: %d", itemsCount.get(), saved.get(), count.get() - saved.get()));
            });
            saveBtn.setEnabled(false);
        });
        return page;
    }

    private T fromRow(Row row, List<FieldToColumnMapping> mappedFields) {
        T entity;
        try {
            entity = domainType.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        T finalEntity = entity;
        mappedFields.forEach(fm -> {
            var field = fm.getField();
            var cell = row.getCell(fm.getSheetColumnNumber());
            if (cell == null ||
                cell.getCellType().equals(CellType.BLANK) ||
                cell.getCellType().equals(CellType.ERROR) ||
                cell.getCellType().equals(CellType._NONE)) return;
            Object value = null;
            try {
                if (LocalDate.class.isAssignableFrom(field.getType())) {
                    if (cell.getLocalDateTimeCellValue() != null)
                        value = cell.getLocalDateTimeCellValue().toLocalDate();
                } else if (LocalDateTime.class.isAssignableFrom(field.getType())) {
                    value = cell.getLocalDateTimeCellValue();
                } else if (Integer.class.isAssignableFrom(field.getType()) || int.class.isAssignableFrom(field.getType())) {
                    value = (int) cell.getNumericCellValue();
                } else if (Double.class.isAssignableFrom(field.getType()) || double.class.isAssignableFrom(field.getType())) {
                    value = cell.getNumericCellValue();
                } else if (Boolean.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType())) {
                    value = cell.getBooleanCellValue();
                } else if (String.class.isAssignableFrom(field.getType())) {
                    value = cell.getStringCellValue();
                } else if (field.getType().isEnum()) {
                    value = Enum.valueOf((Class<Enum>) field.getType(), cell.getStringCellValue());
                } else if (AbstractEntity.class.isAssignableFrom(field.getType())) {
                    var repo = (ExtendedJpaRepository) repositories.getRepositoryFor(field.getType()).get();
                    if (cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue() != null)
                        value = repo.findBySimpleNaturalId(cell.getStringCellValue()).get();
                    else value = repo.findById((int) cell.getNumericCellValue()).get();
                }
                BeanUtils.setProperty(finalEntity, field.getName(), value);
            } catch (Exception e) {
                Utils.showErrorNotification("Row number %d. Failed to set %s value. Cause: %s", cell.getRowIndex() + 1, field.getName(), e.getMessage());
            }
        });
        return finalEntity;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FieldToColumnMapping {
        private Field field;
        private String sheetColumnName;
        private Integer sheetColumnNumber;
    }
}
