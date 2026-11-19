package com.projet.auto_battler.documentation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// The schema-docs profile writes target/jpa-schema.sql. This test only checks that the file exists.
@SpringBootTest
@ActiveProfiles("schema-docs")
@EnabledIfSystemProperty(named = "documentation.schema.export", matches = "true")
class JpaSchemaExportTest {

    @Test
    void exportsSchemaFromJpaMetadata() {
        assertTrue(Files.isRegularFile(Path.of("target/jpa-schema.sql")),
                "Hibernate should export the JPA schema to target/jpa-schema.sql");
    }
}
