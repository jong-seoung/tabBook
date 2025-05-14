package tabbook.server.controller;

import java.util.function.Function;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class SchedulerTestController {

    private final Function<String, String> scheduledTask;

    @GetMapping("/run-scheduler")
    public String runScheduler() {
        return scheduledTask.apply("manual-test");
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
    
}