plugins {
    id("java")
}

group = "org.university"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.hibernate.orm:hibernate-core:7.1.0.Final")
    implementation("com.mysql:mysql-connector-j:9.4.0")
    implementation ("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation ("org.hibernate.validator:hibernate-validator:9.1.0.Final")
    implementation("org.hibernate.validator:hibernate-validator-annotation-processor:9.1.0.Final")
    implementation("org.glassfish.expressly:expressly:6.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")
    annotationProcessor("org.hibernate.orm:hibernate-processor:7.1.0.Final")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.h2database:h2:2.3.232")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}