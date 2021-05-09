package com.dash95.learning.CamelSrpringBoot;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.checkerframework.checker.units.qual.A;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;


@SpringBootApplication
public class CamelSrpringBootApplication extends RouteBuilder {

    public static void main(String[] args) {
        SpringApplication.run(CamelSrpringBootApplication.class, args);
    }

    @Override
    public void configure() throws Exception {
        System.out.println("Started");
//        moveSpecificFiles("sample.txt");
//        moveIFFileContains("ding dong");
        copyToCSV("myList.txt");
        System.out.println("Done");
    }

    private void moveIFFileContains(String content) {
        from("file:src/main/resources/inputFolder/?noop=true").filter(body().convertToString().contains(content))
                .to("file:src/main/resources/outputFolder");
    }

    private void moveFiles() {
        from("file:src/main/resources/inputFolder/?noop=true")
                .to("file:src/main/resources/outputFolder");
    }

    private void moveSpecificFiles(String fileName) {
        from("file:src/main/resources/inputFolder/?noop=true").filter(header(Exchange.FILE_NAME).isEqualTo(fileName))
                .to("file:src/main/resources/outputFolder");
    }

    private void copyToCSV(String fileName) {
        from("file:src/main/resources/inputFolder/?noop=true").filter(header(Exchange.FILE_NAME).isEqualTo(fileName))
                .process(p -> {
                    String body = p.getIn().getBody(String.class);
                    StringBuilder stringBuilder = new StringBuilder();
                    Arrays.stream(body.split(" ")).forEach(s -> {
                        stringBuilder.append(s + ",");
                    });
                    p.getIn().setBody(stringBuilder);
                }).to("file:src/main/resources/outputFolder/?fileName=records.csv");
    }

    public void multiFileProcessor() {
        from("file:source?noop=true").unmarshal().csv().split(body().tokenize(",")).choice()
                .when(body().contains("Closed")).to("file:destination?fileName=close.csv")
                .when(body().contains("Pending")).to("file:destination?fileName=Pending.csv")
                .when(body().contains("Interest")).to("file:destination?fileName=Interest.csv");

    }
}
