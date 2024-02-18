package com.eunycesoft.psms;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Constants {

    public static final String CURRENT_SCHOOL_YEAR = "2022-2023";
    public static final String COOKIE_UI_LANGUAGE = "UI_LANGUAGE";
    public static final String integerFormat = "%,d";
    public static final String markFormat = "%05.2f";
    public static final String percentFormat = "%05.2f %%";
    public static final String averagePattern = "00.00";
    public static final String datePattern = "dd/MM/yyyy";
    public static final String dateTimePattern = "dd/MM/yyyy HH:mm";

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static Locale defaultLocale = Locale.FRENCH;
    public static final String STUDENT_SCHOOL_CERTIFICATE_URL_PATTERN = "report/school-certificate?studentId=%d";
    public static final String GLOBAL_STUDENTS_LIST_URL_PATTERN = "report/students-list";
    public static final String SECTION_STUDENTS_LIST_URL_PATTERN = "report/students-list?section=%s";
    public static final String CLASSROOM_STUDENTS_LIST_URL_PATTERN = "report/students-list?classroom=%s";
    public static final String STUDENT_RECEIPT_URL_PATTERN = "report/student-receipt?studentId=%d&fees=%s";
    public static final String FINANCIAL_REPORT_URL_PATTERN = "report/financial-summary?granularity=%s&fees=%s";
    public static final String STUDENTS_BADGES_REPORT_URL_PATTERN = "report/students-badges";
    public static final String PERSONNELS_BADGES_REPORT_URL_PATTERN = "report/personnels-badges";
    public static final String CLASSROOM_RESULT_SHEET_URL_PATTERN = "report/result-sheet?period=%s&classroom=%s&lang=%s";


    public static final String ANNUAL_CLASSROOM_REPORT_CARDS_URL_PATTERN = "report/annual-class-report-cards?classId=%d&lang=%s";
    public static final String PERIOD_CLASSROOM_REPORT_CARDS_URL_PATTERN = "report/period-class-report-cards?periodId=%d&classId=%d&lang=%s";


    public static final String CLASSROOM_MARK_SHEET_URL_PATTERN = "classrooms/mark-sheet?period=%s&section=%s&classroom=%s&lang=%s";
    public static final String TERM_TOP_10_URL_PATTERN = "classrooms/top-ten?period=%s";
    public static final String TERM_LAST_10_URL_PATTERN = "classrooms/last-ten?period=%s";
    public static final String TERM_CLASSES_RANKING_URL_PATTERN = "classrooms/classes-ranking?period=%s";
    public static final String TERM_MARK_SHEET = "report/marks-sheet?period=%s&classroom=%s&lang=%s";
    public static final String TERM_CLASS_TOP_N_STUDENTS = "report/class-top-n-students?period=%s&number=%s";
    public static final String TERM_CLASS_REPORT_CARD = "report/report-card?period=%s&classroom=%s&lang=%s";

    public static final String REPORT_DISCHARGE_LIST_URL_PATTERN = "classrooms/report-discharge-list?period=%s";
}
