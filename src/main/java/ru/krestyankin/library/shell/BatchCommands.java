package ru.krestyankin.library.shell;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import static ru.krestyankin.library.config.JobConfig.*;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;

    @SneakyThrows
    @ShellMethod(value = "start convert", key = "start")
    public void startConvertJob(){
        Long executionId = jobOperator.start(CONVERT_LIBRARY_JOB_NAME,"");
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "show info", key = "info")
    public void showInfo() {
        System.out.println(jobExplorer.getJobNames());
        System.out.println(jobExplorer.getLastJobInstance(CONVERT_LIBRARY_JOB_NAME));
    }
}
