package com.eunycesoft.psms.views;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.entity.StudentFee;
import com.eunycesoft.psms.data.entity.StudentPayment;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Fee;
import com.eunycesoft.psms.data.enums.Section;
import com.eunycesoft.psms.views.components.DateRangePayments;
import com.eunycesoft.psms.views.components.FinancialReportPrintDialog;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@PageTitle("Burser dashboard")
@Route(value = "BurserDashboard", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BURSER", "HEADMASTER+"})
public class BurserDashboardView extends Crud<Student> {
    protected ComboBox<Section> sectionFilter = new ComboBox<>();
    protected ComboBox<Classroom> classroomFilter = new ComboBox<>();
    protected StudentFeeCrud feesEditor = new StudentFeeCrud();
    protected StudentPaymentsCrud paymentsEditor = new StudentPaymentsCrud();

    protected SplitLayout financialForm = new SplitLayout(feesEditor, paymentsEditor, SplitLayout.Orientation.VERTICAL);

    public BurserDashboardView() {
        super(Student.class);
        buildUI();
        buildLogic();
    }

    @Override
    public void onGridSelectionChanged() {
        if (form != null && form.isAttached()) form.close();
        if (financialForm.isAttached()) formLayout.remove(financialForm);
        selected = grid.asSingleSelect().getValue();
        updateButtons();
        if (selected != null) {
            feesEditor.setStudent(selected);
            paymentsEditor.setStudent(selected);
            formLayout.add(financialForm);
        }
    }

    private void buildUI() {
        setGridVisibleColumns(List.of("classroom", "name", "surname"));
        Map.of(sectionFilter, "Section", classroomFilter, "Classroom").forEach((key, value) -> {
            key.setPlaceholder(value);
            key.setClearButtonVisible(true);
            key.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        });

        gridHeader.remove(columnToggle);
        gridHeader.addComponentAtIndex(0, classroomFilter);
        gridHeader.addComponentAtIndex(0, sectionFilter);

        financialForm.setSizeFull();
    }

    public void buildLogic() {
        addAttachListener(e -> sectionFilter.setItems(Section.values()));
        sectionFilter.addValueChangeListener(evt -> {
            if (evt.getValue() != null) classroomFilter.setItems(evt.getValue().getClassrooms());
            else classroomFilter.clear();
            refreshGrid();
        });
        classroomFilter.addValueChangeListener(evt -> {
            refreshGrid();
        });

        feesEditor.addCrudOperationListener(evt -> {
            refreshSelected();
            feesEditor.setStudent(selected);
            paymentsEditor.setStudent(selected);
        });

        paymentsEditor.addCrudOperationListener(evt -> {
            refreshSelected();
            paymentsEditor.setStudent(selected);
        });
    }

    @Override
    protected void configureContextMenu() {
        super.configureContextMenu();
        contextMenu.add(new Hr());
        var financesMenu = contextMenu.addItem("Finances");
        financesMenu.getSubMenu().addItem("Financial report", evt -> {
            new FinancialReportPrintDialog().show();
        });
        financesMenu.getSubMenu().addItem("Payments on date range", evt -> {
            new DateRangePayments().show();
        });
    }

    @Override
    public Collection<Student> findAll() {
        if (sectionFilter.getValue() == null) {
            return repo.findAll();
        } else if (classroomFilter.getValue() == null) {
            return sectionFilter.getValue().getStudents();
        }
        return classroomFilter.getValue().getStudents();
    }

    protected class StudentFeeCrud extends Crud<StudentFee> {
        private Student student;

        public StudentFeeCrud() {
            super(StudentFee.class, false, false);
            gridHeader.remove(columnToggle);
            gridHeaderCaption.setText("Student fees");
            grid.removeColumnByKey("student");
            grid.removeColumnByKey("discountReason");
            setGridRefreshOnFormSubmit(false);
        }

        public void setStudent(Student student) {
            this.student = student;
            refreshGrid();
        }

        @Override
        public Collection<StudentFee> findAll() {
            var list= student.getStudentFees();
            list.sort(Comparator.comparingInt(sf->sf.getFee().getId()));
            return list;
        }

        @Override
        public void refreshGrid() {
            super.refreshGrid();
            if (student == null) return;
            int raw = student.getRawAmountToPay(Arrays.asList());
            int discount = student.getDiscount(Arrays.asList());
            grid.getColumnByKey("amount")
                    .setFooter(new Html(String.format("<b>%,d</b>", raw)));
            grid.getColumnByKey("discount")
                    .setFooter(new Html(String.format("<b>%,d</b>", discount)));
            grid.getColumnByKey("netToPay")
                    .setFooter(new Html(String.format("<b>%,d</b>", raw - discount)));
        }

        @Override
        public void configureCreatedForm() {
            switch (form.getOperation()) {
                case CREATE -> {
                    form.getFieldsMapping().get("student").setValue(student);
                    form.getContent().remove(form.getFieldsMapping().get("student"));
                    var feeField = (ComboBox<Fee>) form.getFieldsMapping().get("fee");
                    feeField.setItems(student.getUnregisteredFees());
                }
            }
        }
    }

    protected class StudentPaymentsCrud extends Crud<StudentPayment> {
        private Student student;

        public StudentPaymentsCrud() {
            super(StudentPayment.class, false, false);
            gridHeader.remove(columnToggle);
            gridHeaderCaption.setText("Student payments");
            grid.removeColumnByKey("student");
            setGridRefreshOnFormSubmit(false);
        }

        public void setStudent(Student student) {
            this.student = student;
            refreshGrid();
        }

        @Override
        public void refreshGrid() {
            super.refreshGrid();
            if (student == null) return;
            var total = student.getTotalPaid(Arrays.asList());
            grid.getColumnByKey("fee")
                    .setFooter(new Html(String.format("<b>Total paid: %,d</b>", total)));
            grid.getColumnByKey("paymentDate")
                    .setFooter(new Html(String.format("<b>Left to pay: %,d</b>", student.getNetToPay(Arrays.asList()) - total)));
        }

        @Override
        public Collection<StudentPayment> findAll() {
            var sp = student.getStudentPayments();
            sp.sort(Comparator.<StudentPayment>comparingInt(item -> item.getFee().getId())
                    .thenComparing(StudentPayment::getPaymentDate));
            return student.getStudentPayments();
        }

        @Override
        public void configureCreatedForm() {
            switch (form.getOperation()) {
                case CREATE -> configureAddForm();
                case UPDATE -> configureUpdateForm();
            }
        }

        private void configureAddForm() {
            form.getContent().removeAll();
            form.getBinder().readBean(null);
            var totalRemainder = student.getLeftToPay(Arrays.asList());
            if (totalRemainder == 0) {
                form.getContent().add(new Text("No fee to pay for this student"));
                return;
            }
            var map = new HashMap<Fee, IntegerField>();
            var studentFees = student.getStudentFees();
            studentFees.sort(Comparator.comparingInt(sf->sf.getFee().getId()));
            studentFees.forEach(sf -> {
                var leftToPay = student.getLeftToPay(Arrays.asList(sf.getFee()));
                if (leftToPay == 0) return;
                var field = new IntegerField(sf.getFee().toString());
                field.setHelperText(String.format("Left to pay: %,d", leftToPay));
                field.setMax(leftToPay);
                if (sf.getFee() == Fee.REG)
                    field.setValue(leftToPay);
                map.put(sf.getFee(), field);
                form.getContent().add(field);
            });
            var add = new Button("Add", VaadinIcon.PLUS.create(), evt -> {
                if (map.isEmpty()) return;
                AtomicInteger count = new AtomicInteger();
                map.forEach((fee, field) -> {
                    if (field.getValue() != null && field.getValue() > 0 && !field.isInvalid()) {
                        repo.save(new StudentPayment(student, fee, field.getValue()));
                        count.incrementAndGet();
                    }
                });
                Utils.showSuccessNotification(count.get() + " student payment(s) saved successfully");
                fireEvent(new CrudEvent<>(this, false, form.getBean(), form.getOperation()));
                form.close();
            });
            form.getFooter().remove(form.getSubmitButton());
            add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            add.addClickShortcut(Key.ENTER);
            form.getFooter().add(add);
        }

        private void configureUpdateForm() {
            var amountField = (IntegerField) form.getFieldsMapping().get("amount");
            var fee = form.getBean().getFee();
            int max = student.getLeftToPay(Arrays.asList(fee)) + form.getBean().getAmount();
            form.getBinder().forField(amountField)
                    .withValidator(amount -> amount <= max, String.format("The amount must be <= %,d", max))
                    .bind("amount");
            amountField.setHelperText("Max left for this payment: " + NumberFormat.getIntegerInstance().format(max));
        }
    }
}