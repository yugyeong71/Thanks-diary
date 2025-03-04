package com.example.thanksdiary.common;

import org.apache.catalina.security.SecurityConfig;
import org.junit.Ignore;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit4.SpringRunner;

@Import(SecurityConfig.class)
@ExtendWith(RestDocumentationExtension.class)
@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@Ignore
public class ControllerTest {
}
