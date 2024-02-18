package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Constants;
import com.eunycesoft.psms.data.enums.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
public class ReportController {

    @GetMapping(value = "/school-certificate")
    public byte[] schoolCertificate(@RequestParam Integer studentId) {
        return new SchoolCertificateBuilder(studentId).buildReport().exportToPdf();
    }

    @GetMapping(value = "/students-list")
    public byte[] provisionalList(@RequestParam Optional<Section> section,
                                  @RequestParam Optional<Classroom> classroom,
                                  @CookieValue(Constants.COOKIE_UI_LANGUAGE) String lang) {
        return new StudentsListBuilder(section, classroom, lang).buildReport().exportToPdf();
    }

    @GetMapping(value = "/student-receipt")
    public byte[] studentReceipt(@RequestParam int studentId, @RequestParam List<Fee> fees) {
        return new StudentReceiptBuilder(studentId, fees).buildReport().exportToPdf();
    }

    @GetMapping(value = "/financial-summary")
    public byte[] financialReport(@RequestParam String granularity, @RequestParam List<Fee> fees, @CookieValue(Constants.COOKIE_UI_LANGUAGE) String lang) {
        var gran = FinancialReportGranularity.valueOf(granularity);
        return new FinancialReportBuilder(gran, fees, lang).buildReport().exportToPdf();
    }

    @GetMapping(value = "/students-badges")
    public byte[] studentsBadges() {
        return new StudentsBadgesBuilder().buildReport().exportToPdf();
    }

    @GetMapping(value = "/personnels-badges")
    public byte[] personnelsBadges() {
        return new PersonnelsBadgesBuilder().buildReport().exportToPdf();
    }

    @GetMapping(value = "/marks-sheet")
    public byte[] marksheet(@RequestParam String classroom, @RequestParam String period, @RequestParam String lang) {
        var p = Period.valueOf(period);
        if (p == Period.AN)
            return new AnnualMarksheetBuilder(Classroom.valueOf(classroom), p, lang).buildReport().exportToPdf();
        else return new TermMarksheetBuilder(Classroom.valueOf(classroom), p, lang).buildReport().exportToPdf();
    }

    @GetMapping(value = "/result-sheet")
    public byte[] resultSheet(@RequestParam String classroom, @RequestParam String period, @RequestParam String lang) {
        var p = Period.valueOf(period);
        if (p == Period.AN)
            return new AnnualClassResultSheetBuilder(p, Classroom.valueOf(classroom), lang).buildReport().exportToPdf();
        else return new TermClassResultSheetBuilder(p, Classroom.valueOf(classroom), lang).buildReport().exportToPdf();
    }

    @GetMapping(value = "/class-top-n-students")
    public byte[] termTopN(@RequestParam String period, @RequestParam Integer number, @CookieValue(Constants.COOKIE_UI_LANGUAGE) String lang) {
        var p = Period.valueOf(period);
        if (p == Period.AN)
            return new AnnualTopNBuilder(p, number, lang).buildReport().exportToPdf();
        else return new TermTopNBuilder(p, number, lang).buildReport().exportToPdf();
    }

    @GetMapping(value = "/report-card")
    public byte[] reportCard(@RequestParam String period, @RequestParam String classroom, @RequestParam String lang) {
        var p = Period.valueOf(period);
        if (p == Period.AN)
            return new AnnualClassReportCardsBuilder(p, Classroom.valueOf(classroom), lang).buildReport().exportToPdf();
        else return new TermClassReportCardsBuilder(p, Classroom.valueOf(classroom), lang).buildReport().exportToPdf();
    }
}