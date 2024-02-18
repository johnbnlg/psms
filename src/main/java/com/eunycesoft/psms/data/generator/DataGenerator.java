package com.eunycesoft.psms.data.generator;

import com.eunycesoft.psms.data.entity.*;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.repository.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.support.Repositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.eunycesoft.psms.Application.repositories;
import static com.eunycesoft.psms.data.enums.Classroom.*;
import static com.eunycesoft.psms.data.enums.Fee.*;
import static com.eunycesoft.psms.data.enums.Gender.F;
import static com.eunycesoft.psms.data.enums.Gender.M;
import static com.eunycesoft.psms.data.enums.Language.EN;
import static com.eunycesoft.psms.data.enums.Language.FR;
import static com.eunycesoft.psms.data.enums.Role.*;
import static com.eunycesoft.psms.data.enums.Section.BIL;
import static com.eunycesoft.psms.data.enums.Subject.*;

@SpringComponent
@Slf4j
public class DataGenerator {

    public DataGenerator() {
    }

    @Bean
    public CommandLineRunner loadData(ListableBeanFactory beanFactory,
                                      UserRepository userRepository,
                                      ClassroomFeeRepository classroomFeeRepository,
                                      ClassroomSubjectRepository classroomSubjectRepository,
                                      ClassroomTeacherRepository classroomTeacherRepository,
                                      PersonnelRepository personnelRepository,
                                      AppreciationRepository appreciationRepository
                                      /*
                                      ,StudentResultBySubjectRepository resultBySubjectRepository,
                                      StudentResultRepository resultRepository,
                                      StudentResultByGroupRepository resultByGroupRepository,
                                      StudentResultByLanguageRepository resultByLanguageRepository
                                      */) {
        return args -> {
            repositories = new Repositories(beanFactory);

            if (userRepository.count() != 0L) {
                log.info("Using existing database");
                return;
            }

            log.info("Generating demo data");

            log.info("******************** Default user ********************");
            var date = LocalDate.of(2018, 12, 16);
            var pw = new BCryptPasswordEncoder().encode("admin");
            userRepository.save(new User("MORP21M0000", "ADMINISTRATOR", "", EN, M, date, "Douala", "Nbwang", "admin", pw, ADMIN));

            if (appreciationRepository.count() == 0) {
                appreciationRepository.saveAll(List.of(
                        new Appreciation(0, 9.9999, "N", "NYE", "Non acquis", "Not yet meeting Expectation"),
                        new Appreciation(10, 14.9999, "B", "AE", "En cours d'acquisition", "Approaching Expectation"),
                        new Appreciation(15, 17.9999, "A", "ME", "Acquis", "Meeting Expectation"),
                        new Appreciation(18, 20, "A+", "AE+", "Expert", "Above Expectation")
                ));
            }

            log.info("******************** Personnel ********************");
            personnelRepository.saveAll(List.of(
//                    new Personnel("MORP16F0001", "AMETENE", "Helene Sandrine", FR, F, SECRETARY),
                    new Personnel("MORP16F0002", "ESPINA", "Lenka", EN, F, TEACHER),
                    new Personnel("MORP16F0003", "FEJIO AGHOKENG", "Marie", FR, F, BURSER),
                    new Personnel("MORP16F0004", "MBIYDZELA", "Marceline", EN, F, TEACHER),
                    new Personnel("MORP16F0005", "MEUTCHEYO", "Christelle K.", EN, F, TEACHER),
                    new Personnel("MORP16M0006", "NGUEFACK", "Casimir", FR, M, TEACHER),
                    new Personnel("MORP16M0007", "MVOKO BELOMO", "Francois", FR, M, DRIVER),
                    new Personnel("MORP16F0008", "NDAMOU", "Adolphine", FR, F, TEACHER),
                    new Personnel("MORP16F0009", "NGONO", "Marthe", FR, F, TEACHER),
                    new Personnel("MORP16M0010", "PETGANG", "Narice", FR, F, TEACHER),
                    new Personnel("MORP16F0011", "TCHIGHEU", "Eveline", FR, F, TEACHER),
//                    new Personnel("MORP16M0012", "VOUKENG", "Hugues", FR, M, Role.WATCHMAN),
                    new Personnel("MORP17F0013", "NGUENA", "Judith", FR, F, HEADMASTER),
                    new Personnel("MORP18F0014", "DJONTU", "Baudine", FR, F, TEACHER),
                    new Personnel("MORP18F0015", "MAFO AGHOKENG", "Solange", FR, F, TEACHER),
                    new Personnel("MORP18M0016", "NGULEFAC", "Gilbert", EN, M, TEACHER),
                    new Personnel("MORP19F0017", "YONTA YEMETIO", "Alvine", FR, F, TEACHER),
                    new Personnel("MORP20M0018", "BILOA", "Dieudonne", FR, M, DRIVER),
                    new Personnel("MORP20M0019", "SOLIKI", "Vincent", FR, M, WATCHMAN),
                    new Personnel("MORP20M0020", "DOUANLA", "Boris", FR, M, TEACHER),
                    new Personnel("MORP20M0021", "KAWAI", "Jackson", EN, M, TEACHER),
//                    new Personnel("MORP20F0022", "MOTEYO", "Rolande", FR, F, TEACHER),
                    new Personnel("MORP20F0023", "MOUKEN", "Aureline", FR, F, TEACHER),
                    new Personnel("MORP20F0024", "NGANKEU", "Hermine Sandrine", FR, F, TEACHER),
                    new Personnel("MORP20M0025", "NGANSOP", "Paul", FR, M, TEACHER),
                    new Personnel("MORP21F0026", "ASAAH", "Delphine", FR, F, TEACHER),
                    new Personnel("MORP21F0027", "FOBID", "Fany Fri", EN, F, TEACHER),
                    new Personnel("MORP21M0028", "TIOFACK", "Gabin", FR, M, TEACHER),
//                    new Personnel("MORP21M0029", "MOLIKI", "Israël", EN, M, TEACHER),
                    new Personnel("MORP21F0030", "CHENDJOU", "Christelle", FR, F, TEACHER),
                    new Personnel("MORP21M0031", "DEMFACK", "Etienne Rodrigue", FR, M, TEACHER),
//                    new Personnel("MORP21M0032", "EFAH", "Lionel Efah", EN, M, TEACHER),
//                    new Personnel("MORP21F0033", "ESAW", "Loise Kongwe", EN, F, TEACHER),
                    new Personnel("MORP21F0034", "ESOFOR", "Constance", EN, F, TEACHER),
                    new Personnel("MORP21F0035", "FOKOUO", "Rolande", FR, F, TEACHER),
                    new Personnel("MORP21M0036", "NGUNY", "Cylinus Fortiangu", EN, M, TEACHER)
            ));


            log.info("******************** Classrooms fees ********************");
            // Grouping classrooms by sections
//            var bilClassrooms = List.of(PN, NS1, NS2, SIL_BIL, CP_BIL, CE1_BIL, CE2_BIL, CM1_BIL, CM2_BIL);
//            var frClassrooms = List.of(SIL, CP, CE1, CE2, CM1, CM2);
//            var enClassrooms = List.of(CLASS1, CLASS2, CLASS3, CLASS4, CLASS5, CLASS6);

            Arrays.stream(Classroom.values()).forEach(cls -> {
                classroomFeeRepository.save(new ClassroomFee(cls, REG, true, 20000));
                if (cls.isExamClass() || cls.getSection() == BIL) {
                    classroomFeeRepository.save(new ClassroomFee(cls, SLIDE1, true, 70000));
                    classroomFeeRepository.save(new ClassroomFee(cls, SLIDE2, true, 70000));
                } else {
                    classroomFeeRepository.save(new ClassroomFee(cls, SLIDE1, true, 50000));
                    classroomFeeRepository.save(new ClassroomFee(cls, SLIDE2, true, 50000));
                }
            });

            Arrays.stream(Classroom.values()).forEach(cls -> {
                switch (cls.getSection()) {
                    case BIL -> {
                        classroomTeacherRepository.save(new ClassroomTeacher(cls, FR, null));
                        classroomTeacherRepository.save(new ClassroomTeacher(cls, EN, null));
                    }
                    case FRA -> classroomTeacherRepository.save(new ClassroomTeacher(cls, FR, null));
                    case ANG -> classroomTeacherRepository.save(new ClassroomTeacher(cls, EN, null));
                }
            });

            classroomFeeRepository.save(new ClassroomFee(CM2, CEP, true, 8000));
            classroomFeeRepository.save(new ClassroomFee(CM2, ENT_SIX, true, 8000));
            classroomFeeRepository.save(new ClassroomFee(CLASS6, FSLC, true, 8000));
            classroomFeeRepository.save(new ClassroomFee(CLASS6, COM_ENT, true, 8000));

            log.info("******************** Classrooms subjects ********************");
            classroomSubjectRepository.saveAll(List.of(

                    // PS/PN
                    new ClassroomSubject(PN, COM_FRE, FR, 20., 0.25, 0.25, 0.25, 0.25),
                    new ClassroomSubject(PN, MATH, FR, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, TECH, FR, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, ICT, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, HG, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, MORAL, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, NAT_LANG, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, SPORT, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, ENTREP, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, ART, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, COM_ENG, EN, 20., 0.25, 0.25, 0.25, 0.25),
                    new ClassroomSubject(PN, MATH, EN, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, TECH, EN, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, ICT, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, HG, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, MORAL, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, NAT_LANG, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, SPORT, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, ENTREP, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(PN, ART, EN, 10., 0.25, null, 0.5, 0.25),

                    // MS/NS1
                    new ClassroomSubject(NS1, COM_FRE, FR, 20., 0.25, 0.25, 0.25, 0.25),
                    new ClassroomSubject(NS1, MATH, FR, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, TECH, FR, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, ICT, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, HG, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, MORAL, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, NAT_LANG, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, SPORT, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, ENTREP, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, ART, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, COM_ENG, EN, 20., 0.25, 0.25, 0.25, 0.25),
                    new ClassroomSubject(NS1, MATH, EN, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, TECH, EN, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, ICT, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, HG, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, MORAL, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, NAT_LANG, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, SPORT, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, ENTREP, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS1, ART, EN, 10., 0.25, null, 0.5, 0.25),

                    // GS/NS2
                    new ClassroomSubject(NS2, COM_FRE, FR, 20., 0.25, 0.25, 0.25, 0.25),
                    new ClassroomSubject(NS2, MATH, FR, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, TECH, FR, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, ICT, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, HG, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, MORAL, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, NAT_LANG, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, SPORT, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, ENTREP, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, ART, FR, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, COM_ENG, EN, 20., 0.25, 0.25, 0.25, 0.25),
                    new ClassroomSubject(NS2, MATH, EN, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, TECH, EN, 20., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, ICT, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, HG, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, MORAL, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, NAT_LANG, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, SPORT, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, ENTREP, EN, 10., 0.25, null, 0.5, 0.25),
                    new ClassroomSubject(NS2, ART, EN, 10., 0.25, null, 0.5, 0.25),

                    // SIL/Class 1
                    new ClassroomSubject(SIL_BIL, SPEAK, FR, 20.),
                    new ClassroomSubject(SIL_BIL, COM_FRE, FR, 40.),
                    new ClassroomSubject(SIL_BIL, MATH, FR, 20.),
                    new ClassroomSubject(SIL_BIL, TECH, FR, 20.),
                    new ClassroomSubject(SIL_BIL, ICT, FR, 10.),
                    new ClassroomSubject(SIL_BIL, MORAL, FR, 20.),
                    new ClassroomSubject(SIL_BIL, NAT_LANG, FR, 10.),
                    new ClassroomSubject(SIL_BIL, SPORT, FR, 20.),
                    new ClassroomSubject(SIL_BIL, ENTREP, FR, 10.),
                    new ClassroomSubject(SIL_BIL, ART, FR, 10.),
                    new ClassroomSubject(SIL_BIL, COM_ENG, EN, 40.),
                    new ClassroomSubject(SIL_BIL, MATH, EN, 40.),
                    new ClassroomSubject(SIL_BIL, TECH, EN, 30.),
                    new ClassroomSubject(SIL_BIL, ICT, EN, 10.),
                    new ClassroomSubject(SIL_BIL, MORAL, EN, 30.),
                    new ClassroomSubject(SIL_BIL, NAT_LANG, EN, 10.),
                    new ClassroomSubject(SIL_BIL, SPORT, EN, 20.),
                    new ClassroomSubject(SIL_BIL, ENTREP, EN, 20.),
                    new ClassroomSubject(SIL_BIL, ART, EN, 20.),

                    // CP/ Class 2
                    new ClassroomSubject(CP_BIL, SPEAK, FR, 20.),
                    new ClassroomSubject(CP_BIL, COM_FRE, FR, 50.),
                    new ClassroomSubject(CP_BIL, MATH, FR, 20.),
                    new ClassroomSubject(CP_BIL, TECH, FR, 20.),
                    new ClassroomSubject(CP_BIL, ICT, FR, 10.),
                    new ClassroomSubject(CP_BIL, MORAL, FR, 20.),
                    new ClassroomSubject(CP_BIL, NAT_LANG, FR, 10.),
                    new ClassroomSubject(CP_BIL, SPORT, FR, 20.),
                    new ClassroomSubject(CP_BIL, ENTREP, FR, 10.),
                    new ClassroomSubject(CP_BIL, ART, FR, 10.),
                    new ClassroomSubject(CP_BIL, COM_ENG, EN, 40.),
                    new ClassroomSubject(CP_BIL, MATH, EN, 40.),
                    new ClassroomSubject(CP_BIL, TECH, EN, 30.),
                    new ClassroomSubject(CP_BIL, ICT, EN, 10.),
                    new ClassroomSubject(CP_BIL, MORAL, EN, 30.),
                    new ClassroomSubject(CP_BIL, NAT_LANG, EN, 10.),
                    new ClassroomSubject(CP_BIL, SPORT, EN, 20.),
                    new ClassroomSubject(CP_BIL, ENTREP, EN, 20.),
                    new ClassroomSubject(CP_BIL, ART, EN, 20.),

                    // CE1/ Class 3
                    new ClassroomSubject(CE1_BIL, SPEAK, FR, 20.),
                    new ClassroomSubject(CE1_BIL, COM_FRE, FR, 40.),
                    new ClassroomSubject(CE1_BIL, MATH, FR, 30.),
                    new ClassroomSubject(CE1_BIL, TECH, FR, 30.),
                    new ClassroomSubject(CE1_BIL, ICT, FR, 20.),
                    new ClassroomSubject(CE1_BIL, HG, FR, 20.),
                    new ClassroomSubject(CE1_BIL, MORAL, FR, 10.),
                    new ClassroomSubject(CE1_BIL, NAT_LANG, FR, 10.),
                    new ClassroomSubject(CE1_BIL, SPORT, FR, 20.),
                    new ClassroomSubject(CE1_BIL, ENTREP, FR, 10.),
                    new ClassroomSubject(CE1_BIL, ART, FR, 10.),
                    new ClassroomSubject(CE1_BIL, COM_ENG, EN, 100.),
                    new ClassroomSubject(CE1_BIL, MATH, EN, 100.),
                    new ClassroomSubject(CE1_BIL, TECH, EN, 30.),
                    new ClassroomSubject(CE1_BIL, ICT, EN, 20.),
                    new ClassroomSubject(CE1_BIL, HG, EN, 20.),
                    new ClassroomSubject(CE1_BIL, MORAL, EN, 30.),
                    new ClassroomSubject(CE1_BIL, NAT_LANG, EN, 10.),
                    new ClassroomSubject(CE1_BIL, SPORT, EN, 20.),
                    new ClassroomSubject(CE1_BIL, ENTREP, EN, 30.),
                    new ClassroomSubject(CE1_BIL, ART, EN, 20.),

                    // CE2/ Class 4
                    new ClassroomSubject(CE2_BIL, SPEAK, FR, 20.),
                    new ClassroomSubject(CE2_BIL, COM_FRE, FR, 50.),
                    new ClassroomSubject(CE2_BIL, MATH, FR, 40.),
                    new ClassroomSubject(CE2_BIL, TECH, FR, 30.),
                    new ClassroomSubject(CE2_BIL, ICT, FR, 20.),
                    new ClassroomSubject(CE2_BIL, HG, FR, 20.),
                    new ClassroomSubject(CE2_BIL, MORAL, FR, 10.),
                    new ClassroomSubject(CE2_BIL, NAT_LANG, FR, 10.),
                    new ClassroomSubject(CE2_BIL, SPORT, FR, 20.),
                    new ClassroomSubject(CE2_BIL, ENTREP, FR, 10.),
                    new ClassroomSubject(CE2_BIL, ART, FR, 10.),
                    new ClassroomSubject(CE2_BIL, COM_ENG, EN, 100.),
                    new ClassroomSubject(CE2_BIL, MATH, EN, 100.),
                    new ClassroomSubject(CE2_BIL, TECH, EN, 30.),
                    new ClassroomSubject(CE2_BIL, ICT, EN, 20.),
                    new ClassroomSubject(CE2_BIL, HG, EN, 20.),
                    new ClassroomSubject(CE2_BIL, MORAL, EN, 30.),
                    new ClassroomSubject(CE2_BIL, NAT_LANG, EN, 20.),
                    new ClassroomSubject(CE2_BIL, SPORT, EN, 20.),
                    new ClassroomSubject(CE2_BIL, ENTREP, EN, 30.),
                    new ClassroomSubject(CE2_BIL, ART, EN, 20.),


                    // SIL
                    new ClassroomSubject(SIL, SPEAK, FR, 20.),
                    new ClassroomSubject(SIL, COM_FRE, FR, 40.),
                    new ClassroomSubject(SIL, COM_ENG, FR, 20.),
                    new ClassroomSubject(SIL, MATH, FR, 20.),
                    new ClassroomSubject(SIL, TECH, FR, 20.),
                    new ClassroomSubject(SIL, ICT, FR, 10.),
                    new ClassroomSubject(SIL, MORAL, FR, 20.),
                    new ClassroomSubject(SIL, NAT_LANG, FR, 10.),
                    new ClassroomSubject(SIL, SPORT, FR, 20.),
                    new ClassroomSubject(SIL, ENTREP, FR, 10.),
                    new ClassroomSubject(SIL, ART, FR, 10.),

                    // CP
                    new ClassroomSubject(CP, SPEAK, FR, 20.),
                    new ClassroomSubject(CP, COM_FRE, FR, 50.),
                    new ClassroomSubject(CP, COM_ENG, FR, 20.),
                    new ClassroomSubject(CP, MATH, FR, 20.),
                    new ClassroomSubject(CP, TECH, FR, 20.),
                    new ClassroomSubject(CP, ICT, FR, 10.),
                    new ClassroomSubject(CP, MORAL, FR, 20.),
                    new ClassroomSubject(CP, NAT_LANG, FR, 10.),
                    new ClassroomSubject(CP, SPORT, FR, 20.),
                    new ClassroomSubject(CP, ENTREP, FR, 10.),
                    new ClassroomSubject(CP, ART, FR, 10.),

                    // CE1
                    new ClassroomSubject(CE1, SPEAK, FR, 20.),
                    new ClassroomSubject(CE1, COM_FRE, FR, 40.),
                    new ClassroomSubject(CE1, COM_ENG, FR, 30.),
                    new ClassroomSubject(CE1, MATH, FR, 30.),
                    new ClassroomSubject(CE1, TECH, FR, 30.),
                    new ClassroomSubject(CE1, ICT, FR, 20.),
                    new ClassroomSubject(CE1, HG, FR, 20.),
                    new ClassroomSubject(CE1, MORAL, FR, 10.),
                    new ClassroomSubject(CE1, NAT_LANG, FR, 10.),
                    new ClassroomSubject(CE1, SPORT, FR, 20.),
                    new ClassroomSubject(CE1, ENTREP, FR, 10.),
                    new ClassroomSubject(CE1, ART, FR, 10.),

                    // CE2
                    new ClassroomSubject(CE2, SPEAK, FR, 20.),
                    new ClassroomSubject(CE2, COM_FRE, FR, 50.),
                    new ClassroomSubject(CE2, COM_ENG, FR, 20.),
                    new ClassroomSubject(CE2, MATH, FR, 40.),
                    new ClassroomSubject(CE2, TECH, FR, 30.),
                    new ClassroomSubject(CE2, ICT, FR, 20.),
                    new ClassroomSubject(CE2, HG, FR, 20.),
                    new ClassroomSubject(CE2, MORAL, FR, 10.),
                    new ClassroomSubject(CE2, NAT_LANG, FR, 10.),
                    new ClassroomSubject(CE2, SPORT, FR, 20.),
                    new ClassroomSubject(CE2, ENTREP, FR, 10.),
                    new ClassroomSubject(CE2, ART, FR, 10.),

                    // CM1
                    new ClassroomSubject(CM1, SPEAK, FR, 20.),
                    new ClassroomSubject(CM1, COM_FRE, FR, 70.),
                    new ClassroomSubject(CM1, COM_ENG, FR, 50.),
                    new ClassroomSubject(CM1, MATH, FR, 70.),
                    new ClassroomSubject(CM1, TECH, FR, 50.),
                    new ClassroomSubject(CM1, ICT, FR, 20.),
                    new ClassroomSubject(CM1, HG, FR, 40.),
                    new ClassroomSubject(CM1, MORAL, FR, 20.),
                    new ClassroomSubject(CM1, NAT_LANG, FR, 10.),
                    new ClassroomSubject(CM1, SPORT, FR, 20.),
                    new ClassroomSubject(CM1, ENTREP, FR, 10.),
                    new ClassroomSubject(CM1, ART, FR, 10.),

                    // CM2
                    new ClassroomSubject(CM2, SPEAK, FR, 20.),
                    new ClassroomSubject(CM2, COM_FRE, FR, 70.),
                    new ClassroomSubject(CM2, COM_ENG, FR, 50.),
                    new ClassroomSubject(CM2, MATH, FR, 70.),
                    new ClassroomSubject(CM2, TECH, FR, 50.),
                    new ClassroomSubject(CM2, ICT, FR, 20.),
                    new ClassroomSubject(CM2, HG, FR, 50.),
                    new ClassroomSubject(CM2, MORAL, FR, 20.),
                    new ClassroomSubject(CM2, NAT_LANG, FR, 10.),
                    new ClassroomSubject(CM2, SPORT, FR, 20.),
                    new ClassroomSubject(CM2, ENTREP, FR, 10.),
                    new ClassroomSubject(CM2, ART, FR, 10.),

                    // Class 1
                    new ClassroomSubject(CLASS1, COM_FRE, EN, 20.),
                    new ClassroomSubject(CLASS1, COM_ENG, EN, 40.),
                    new ClassroomSubject(CLASS1, MATH, EN, 40.),
                    new ClassroomSubject(CLASS1, TECH, EN, 30.),
                    new ClassroomSubject(CLASS1, ICT, EN, 10.),
                    new ClassroomSubject(CLASS1, MORAL, EN, 30.),
                    new ClassroomSubject(CLASS1, NAT_LANG, EN, 10.),
                    new ClassroomSubject(CLASS1, SPORT, EN, 20.),
                    new ClassroomSubject(CLASS1, ENTREP, EN, 20.),
                    new ClassroomSubject(CLASS1, ART, EN, 20.),

                    // Class 2
                    new ClassroomSubject(CLASS2, COM_FRE, EN, 20.),
                    new ClassroomSubject(CLASS2, COM_ENG, EN, 40.),
                    new ClassroomSubject(CLASS2, MATH, EN, 40.),
                    new ClassroomSubject(CLASS2, TECH, EN, 30.),
                    new ClassroomSubject(CLASS2, ICT, EN, 10.),
                    new ClassroomSubject(CLASS2, MORAL, EN, 30.),
                    new ClassroomSubject(CLASS2, NAT_LANG, EN, 10.),
                    new ClassroomSubject(CLASS2, SPORT, EN, 20.),
                    new ClassroomSubject(CLASS2, ENTREP, EN, 20.),
                    new ClassroomSubject(CLASS2, ART, EN, 20.),

                    // Class 3
                    new ClassroomSubject(CLASS3, COM_FRE, EN, 30.),
                    new ClassroomSubject(CLASS3, COM_ENG, EN, 100.),
                    new ClassroomSubject(CLASS3, MATH, EN, 100.),
                    new ClassroomSubject(CLASS3, TECH, EN, 30.),
                    new ClassroomSubject(CLASS3, ICT, EN, 20.),
                    new ClassroomSubject(CLASS3, HG, EN, 20.),
                    new ClassroomSubject(CLASS3, MORAL, EN, 30.),
                    new ClassroomSubject(CLASS3, NAT_LANG, EN, 10.),
                    new ClassroomSubject(CLASS3, SPORT, EN, 20.),
                    new ClassroomSubject(CLASS3, ENTREP, EN, 30.),
                    new ClassroomSubject(CLASS3, ART, EN, 20.),

                    // Class 4
                    new ClassroomSubject(CLASS4, COM_FRE, EN, 20.),
                    new ClassroomSubject(CLASS4, COM_ENG, EN, 100.),
                    new ClassroomSubject(CLASS4, MATH, EN, 100.),
                    new ClassroomSubject(CLASS4, TECH, EN, 30.),
                    new ClassroomSubject(CLASS4, ICT, EN, 20.),
                    new ClassroomSubject(CLASS4, HG, EN, 20.),
                    new ClassroomSubject(CLASS4, MORAL, EN, 30.),
                    new ClassroomSubject(CLASS4, NAT_LANG, EN, 10.),
                    new ClassroomSubject(CLASS4, SPORT, EN, 20.),
                    new ClassroomSubject(CLASS4, ENTREP, EN, 30.),
                    new ClassroomSubject(CLASS4, ART, EN, 20.),

                    // Class 5
                    new ClassroomSubject(CLASS5, COM_FRE, EN, 50.),
                    new ClassroomSubject(CLASS5, COM_ENG, EN, 100.),
                    new ClassroomSubject(CLASS5, MATH, EN, 100.),
                    new ClassroomSubject(CLASS5, TECH, EN, 40.),
                    new ClassroomSubject(CLASS5, ICT, EN, 20.),
                    new ClassroomSubject(CLASS5, HG, EN, 30.),
                    new ClassroomSubject(CLASS5, MORAL, EN, 30.),
                    new ClassroomSubject(CLASS5, NAT_LANG, EN, 10.),
                    new ClassroomSubject(CLASS5, SPORT, EN, 20.),
                    new ClassroomSubject(CLASS5, ENTREP, EN, 30.),
                    new ClassroomSubject(CLASS5, ART, EN, 10.),

                    // Class 6
                    new ClassroomSubject(CLASS6, COM_FRE, EN, 50.),
                    new ClassroomSubject(CLASS6, COM_ENG, EN, 100.),
                    new ClassroomSubject(CLASS6, MATH, EN, 100.),
                    new ClassroomSubject(CLASS6, TECH, EN, 40.),
                    new ClassroomSubject(CLASS6, ICT, EN, 20.),
                    new ClassroomSubject(CLASS6, HG, EN, 30.),
                    new ClassroomSubject(CLASS6, MORAL, EN, 30.),
                    new ClassroomSubject(CLASS6, NAT_LANG, EN, 10.),
                    new ClassroomSubject(CLASS6, SPORT, EN, 20.),
                    new ClassroomSubject(CLASS6, ENTREP, EN, 30.),
                    new ClassroomSubject(CLASS6, ART, EN, 10.)
            ));

            log.info("******************** End generating demo data ********************");
        };
    }
}