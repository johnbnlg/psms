package com.eunycesoft.psms.views.components.wizard;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Wizard extends Dialog {

    private List<WizardPage> pages = new ArrayList<>();
    private int currentPageIndex = 0;
    private Button btnCancel = new Button("Cancel", e -> cancel());

    public Wizard(String title) {
        setHeaderTitle(title);
        setDraggable(true);
        setModal(true);
        setResizable(true);
        setCloseOnOutsideClick(false);
        setWidth("500px");
        setHeight("500px");
        btnBack.setEnabled(false);
        getFooter().add(btnBack, btnNext, btnCancel);
    }

    public void next() {
        if (isFinalPage()) close();
        else if (pages.get(currentPageIndex).validatePage()) {
            remove(pages.get(currentPageIndex++));
            setCurrentPage(pages.get(currentPageIndex));
        }
    }    private Button btnNext = new Button("Next", e -> next());

    public void back() {
        remove(pages.get(currentPageIndex--));
        setCurrentPage(pages.get(currentPageIndex));
    }

    public void addPage(WizardPage page) {
        if (pages.isEmpty()) setCurrentPage(page);
        pages.add(page);
    }

    public void cancel() {
        close();
    }    private Button btnBack = new Button("back", e -> back());

    public boolean isFinalPage() {
        return currentPageIndex == pages.size() - 1;
    }

    public void setCurrentPage(WizardPage page) {
        removeAll();
        add(new Text(page.getTitle()), page);
        updateButtons();
    }

    public void updateButtons() {
        btnNext.setText(isFinalPage() ? "Finish" : "Next");
        btnBack.setEnabled(currentPageIndex != 0);
    }






}
