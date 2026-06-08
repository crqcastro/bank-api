package br.com.cesarcastro.bankapi.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "br.com.cesarcastro.bankapi", importOptions = ImportOption.DoNotIncludeTests.class)
@DisplayName("Arquitetura Hexagonal")
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_application =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..", "..adapter..", "..infrastructure..");

    @ArchTest
    static final ArchRule application_must_not_depend_on_adapters =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapter..", "..infrastructure.config..", "..infrastructure.constants..");

    @ArchTest
    static final ArchRule controllers_must_reside_in_adapter_in_web =
            classes().that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .should().resideInAPackage("..adapter.in.web..");

    @ArchTest
    static final ArchRule services_must_reside_in_usecase =
            classes().that().areAnnotatedWith(org.springframework.stereotype.Service.class)
                    .should().resideInAPackage("..application.usecase..");

    @ArchTest
    static final ArchRule jpa_repos_must_reside_in_adapter_out =
            classes().that().areAssignableTo(org.springframework.data.jpa.repository.JpaRepository.class)
                    .and().resideInAPackage("br.com.cesarcastro..")
                    .should().resideInAPackage("..adapter.out.persistence..");
}
