package com.eunycesoft.psms.data.enums;

import lombok.Getter;

@Getter
public enum FinancialReportGranularity {
    INSOLVENT("FS.insolvent_title", "classroom", "student", "shared.student_name"),
    STUDENT("FS.student_title", "classroom", "student", "shared.student_name"),
    CLASSROOM("FS.classroom_title", "section", "classroom", "shared.class"),
    SECTION("FS.section_title", "", "section", "shared.section");

    private final String titleKey, groupColumn, granularityColumn, granularityColumnKey;

    FinancialReportGranularity(String titleKey, String groupColumn, String granularityColumn, String granularityColumnKey) {
        this.titleKey = titleKey;
        this.groupColumn = groupColumn;
        this.granularityColumn = granularityColumn;
        this.granularityColumnKey = granularityColumnKey;
    }
}
